package com.ngoucoorp.cameroonguide.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngoucoorp.cameroonguide.listeners.ClickListener;
import com.ngoucoorp.cameroonguide.listeners.RecyclerTouchListener;
import com.ngoucoorp.cameroonguide.Config;
import com.ngoucoorp.cameroonguide.R;
import com.ngoucoorp.cameroonguide.activities.SubCategoryActivity;
import com.ngoucoorp.cameroonguide.adapters.ItemAdapter;
import com.ngoucoorp.cameroonguide.models.PItemData;
import com.ngoucoorp.cameroonguide.models.PSubCategoryData;
import com.ngoucoorp.cameroonguide.uis.ProgressWheel;
import com.ngoucoorp.cameroonguide.utilities.CacheRequest;
import com.ngoucoorp.cameroonguide.utilities.Utils;
import com.ngoucoorp.cameroonguide.utilities.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */

public class TabFragment extends Fragment {

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     * *------------------------------------------------------------------------------------------------
     */
    public int selectedCityID;
    public int selectedSubCategoryID;
    private Picasso p;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private ItemAdapter mAdapter;
    private ProgressWheel progressWheel;
    private PSubCategoryData subCategoryData;
    private List<PItemData> it;
    private List<PItemData> myDataset;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String jsonStatusSuccess;
    private LinearLayout mainLayout;
    private int currentSize = 0;
    private String sortField = "id";
    private String sortType = "asc";
    private int selectedSortIndex;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - New Instance Function
     * *------------------------------------------------------------------------------------------------
     */
    /*public static TabFragment newInstance(PSubCategoryData subCategoryData, int CityID) {
        TabFragment fragment = new TabFragment();
        fragment.setData(subCategoryData, CityID);
        return fragment;
    }*/
    public TabFragment() {

    }

    public void setData(PSubCategoryData subCategoryData, int selectedCityID, int selectedSortIndex) {
        this.subCategoryData = subCategoryData;
        this.selectedCityID = selectedCityID;
        this.selectedSubCategoryID = subCategoryData.id;
        this.selectedSortIndex = selectedSortIndex;
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - New Instance Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     * *------------------------------------------------------------------------------------------------
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab, container, false);

        initData();

        initUI(view);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        try {
            mRecyclerView = null;
            mLayoutManager = null;
            mAdapter = null;
            progressWheel = null;
            subCategoryData = null;
            it = null;
            myDataset = null;
            swipeRefreshLayout = null;
            jsonStatusSuccess = null;
            //p.shutdown();
            Utils.unbindDrawables(mainLayout);
            super.onDestroy();
        } catch (Exception e) {
            super.onDestroy();
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     * *------------------------------------------------------------------------------------------------
     */

    private void initData() {
        try {
            this.jsonStatusSuccess = getResources().getString(R.string.json_status_success);
            // Inflate the layout for this fragment

            if (this.selectedSortIndex == 0) {
                sortField = "name";
                sortType = "asc";
            } else if (this.selectedSortIndex == 1) {
                sortField = "name";
                sortType = "desc";
            } else if (this.selectedSortIndex == 2) {
                sortField = "added";
                sortType = "asc";
            } else if (this.selectedSortIndex == 3) {
                sortField = "added";
                sortType = "desc";
            } else if (this.selectedSortIndex == 4) {
                sortField = "like_count";
                sortType = "asc";
            } else if (this.selectedSortIndex == 5) {
                sortField = "like_count";
                sortType = "desc";
            }

            Utils.psLog(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedCityID + "/sub_cat_id/" + subCategoryData.id + "/item/all/count/" + Config.PAGINATION + "/form/0/field/" + sortField + "/type/" + sortType);
            requestData(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedCityID + "/sub_cat_id/" + subCategoryData.id + "/item/all/count/" + Config.PAGINATION + "/form/0/field/" + sortField + "/type/" + sortType);

            if (getContext() != null) {
                p = new Picasso.Builder(getContext()).build();
            }

            // For memory control
            //.memoryCache(new LruCache(2000))
            //.build();

        } catch (Exception e) {
            Utils.psErrorLogE("Error in initData.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     * *------------------------------------------------------------------------------------------------
     */

    private void initUI(View view) {
        initProgressWheel(view);
        initRecyclerView(view);
        initSwipeRefreshLayout(view);
        initLoadMore(view);
    }

    private void initLoadMore(View view) {
        try {
            mainLayout = view.findViewById(R.id.tab_layout);
            mAdapter.setOnLoadMoreListener(new ItemAdapter.OnLoadMoreListener() {

                @Override
                public void onLoadMore() {
                    //add progress item

                    try {
                        if (myDataset != null) {
                            int from = myDataset.size();

                            if (currentSize != from) {
                                currentSize = from;
                                myDataset.add(null);
                                mAdapter.notifyItemInserted(myDataset.size() - 1);
                                Log.d("API URL : ", Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + 1 + "/sub_cat_id/" + subCategoryData.id + "/item/all/count/" + Config.PAGINATION + "/form/" + from + "/field/" + sortField + "/type/" + sortType);
                                requestData(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + 1 + "/sub_cat_id/" + subCategoryData.id + "/item/all/count/" + Config.PAGINATION + "/from/" + from + "/field/" + sortField + "/type/" + sortType);
                            }
                        }
                    } catch (Exception e) {
                        Utils.psErrorLog("onLoadMore", e);
                    }
                }

            });

            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });

            startLoading();
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initLoadMore.", e);
        }
    }

    private void initProgressWheel(View view) {
        progressWheel = view.findViewById(R.id.progress_wheel);
    }

    private void initSwipeRefreshLayout(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                stopLoading();
            }
        });
    }

    private void initRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        myDataset = new ArrayList<>();

        mAdapter = new ItemAdapter(getActivity(), myDataset, mRecyclerView, p);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                onItemClicked(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     * *------------------------------------------------------------------------------------------------
     */

    private void requestData(String uri) {

        try {
            CacheRequest cacheRequest = new CacheRequest(0, uri, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    try {
                        final String jsonString = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers));
                        JSONObject jsonObject = new JSONObject(jsonString);

                        String status = jsonObject.getString("status");
                        if (status.equals(jsonStatusSuccess)) {

                            if (myDataset != null) {
                                if (myDataset.size() > 0) {
                                    myDataset.remove(myDataset.size() - 1);
                                    mAdapter.notifyItemRemoved(myDataset.size());
                                }
                            }

                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<PItemData>>() {
                            }.getType();
                            it = gson.fromJson(jsonObject.getString("data"), listType);

                            progressWheel.setVisibility(View.GONE);

                            myDataset.addAll(it);

                            stopLoading();

                            if (myDataset != null) {
                                mAdapter.notifyItemInserted(myDataset.size());
                            }
                            mAdapter.setLoaded();


                        } else {
                            if (myDataset != null) {
                                if (myDataset.size() > 0) {
                                    myDataset.remove(myDataset.size() - 1);
                                    mAdapter.notifyItemRemoved(myDataset.size());
                                }
                            }
                            stopLoading();
                            Utils.psLog("Error in loading Item Grid.");
                        }

                    } catch (UnsupportedEncodingException | JSONException e) {
                        stopLoading();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        Utils.psLog(error.getMessage());
                    }catch (Exception e) {
                        Utils.psErrorLog("onErrorResponse", e);
                    }

                }
            });

            cacheRequest.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VolleySingleton.getInstance(getActivity()).addToRequestQueue(cacheRequest);

        } catch (Exception e) {
            Utils.psErrorLog("requestData", e);
        }

    }

    private void startLoading() {
        try {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        } catch (Exception e) {
            Utils.psErrorLog("startLoading", e);
        }
    }

    private void stopLoading() {
        try {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        } catch (Exception e) {
            Utils.psErrorLog("stopLoading", e);
        }

    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     * *------------------------------------------------------------------------------------------------
     */

    public void onItemClicked(int position) {
        try {
            if (getActivity() != null) {
                ((SubCategoryActivity) getActivity()).openActivity(myDataset.get(position).id);
            }
        } catch (Exception e) {
            Utils.psErrorLog("onItemClicked", e);
        }
    }

    public void refreshLikeAndReview(int itemID, String likeCount, String reviewCount) {
        try {
            mAdapter.updateItemLikeAndReviewCount(itemID, likeCount, reviewCount);
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Utils.psErrorLog("refreshLikeAndReview", e);
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/


}
