package com.ngoucoorp.cameroonguide.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ngoucoorp.cameroonguide.models.CategoryRowData;
import com.ngoucoorp.cameroonguide.utilities.Utils;
import com.ngoucoorp.cameroonguide.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */
public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CategoryRowData> categoryRowDataList;

    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener = null;
    private Picasso p;
    private final int VIEW_ITEM = 1;
    private Activity activity;

    //private boolean isFooterEnabled = true;


    private class ProgressViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    public CategoryAdapter(Context context, final List<CategoryRowData> myDataSet, RecyclerView recyclerView, Picasso p) {
        this.activity = (Activity) context;

        categoryRowDataList = myDataSet;

        this.p = p;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    try {
                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            // End has been reached
                            // Do something
                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                            loading = true;
                        }
                    } catch (Exception e) {
                        Utils.psErrorLog("onScrolled", e);
                    }
                }
            });
        } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    try {
                        totalItemCount = gridLayoutManager.getItemCount();
                        lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
                        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            // End has been reached
                            // Do something
                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                            loading = true;
                        }
                    } catch (Exception e) {
                        Utils.psErrorLog("onScrolled", e);
                    }
                }
            });

        } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {

            final StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    try {
                        if (myDataSet != null) {
                            if (myDataSet.size() > 0) {

                                totalItemCount = staggeredGridLayoutManager.getItemCount();

                                int greatestItem = 0;
                                if (totalItemCount == 1) {
                                    Utils.psLog("Total Item Count 1");
                                } else {

                                    // for staggeredGridLayoutManager
                                    int[] arr = new int[totalItemCount];
                                    int[] lastVisibleItem2 = staggeredGridLayoutManager.findLastVisibleItemPositions(arr);

                                    //int greatestItem = 0;
                                    for (int aLastVisibleItem2 : lastVisibleItem2) {
                                        if (aLastVisibleItem2 > greatestItem) {
                                            greatestItem = aLastVisibleItem2;
                                        }
                                    }
                                    if (!loading && totalItemCount <= (greatestItem + visibleThreshold)) {
                                        // End has been reached
                                        // Do something
                                        if (onLoadMoreListener != null) {
                                            onLoadMoreListener.onLoadMore();
                                        }
                                        loading = true;
                                    }

                                }
                            }
                        }
                    } catch (Exception e) {
                        Utils.psErrorLog("onScrolled", e);
                    }

                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROG = 0;
        return categoryRowDataList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        RecyclerView.ViewHolder vh;

        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_row, parent, false);

            vh = new MyViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_item, parent, false);

            vh = new ProgressViewHolder(v);
        }

        return vh;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (categoryRowDataList != null && categoryRowDataList.size() > 0) {
            if (holder instanceof MyViewHolder) {

                ((MyViewHolder) holder).title.setText(categoryRowDataList.get(position).getCatName());

                ((MyViewHolder) holder).title.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

                ((MyViewHolder) holder).icon.destroyDrawingCache();

//                p.load(Config.APP_IMAGES_URL + categoryRowDataList.get(position).getCatImage())
//                        .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
//                        .placeholder(R.drawable.ps_icon)
//                        .into(((MyViewHolder) holder).icon);

                Utils.bindImage(((MyViewHolder) holder).title.getContext(), p, ((MyViewHolder) holder).icon, categoryRowDataList.get(position).getCatImage(), 2);

                ((MyViewHolder) holder).icon.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));


            } else {
                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }
        }
    }


    @Override
    public int getItemCount() {
        if (categoryRowDataList != null) {
            return categoryRowDataList.size();
        }
        return 0;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public static final class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView icon;

        MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.category_name);
            icon = itemView.findViewById(R.id.category_image);

            Context context = title.getContext();

            if(context != null) {
                title.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
            }
        }
    }
}


