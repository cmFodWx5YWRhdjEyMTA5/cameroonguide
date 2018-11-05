package com.ngoucoorp.cameroonguide.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.ngoucoorp.cameroonguide.listeners.ClickListener;
import com.ngoucoorp.cameroonguide.listeners.RecyclerTouchListener;
import com.ngoucoorp.cameroonguide.models.CategoryRowData;
import com.ngoucoorp.cameroonguide.models.PCategoryData;
import com.ngoucoorp.cameroonguide.models.PCityData;
import com.ngoucoorp.cameroonguide.GlobalData;
import com.ngoucoorp.cameroonguide.R;
import com.ngoucoorp.cameroonguide.adapters.CategoryAdapter;
import com.ngoucoorp.cameroonguide.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */
public class SelectedCityActivity extends AppCompatActivity {

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     * *------------------------------------------------------------------------------------------------
     */
    private Picasso p;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView detailImage;
    private StaggeredGridLayoutManager mLayoutManager;
    private CategoryAdapter mAdapter;
    private List<CategoryRowData> categoryRowDataList = new ArrayList<>();
    private int selectedCityID;
    private PCityData pCity;
    private CoordinatorLayout mainLayout;
    private RecyclerView mRecyclerView;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     * *------------------------------------------------------------------------------------------------
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_city);

        try {
            initUI();

            initData();

            saveSelectedCityInfo(pCity);

            bindData();

            loadCategoryGrid();

        }catch (Exception e) {
            Utils.psErrorLog("onCreate", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_news) {


            Utils.psLog("Open News Activity");
            final Intent intent;
            intent = new Intent(this, NewsListActivity.class);
            intent.putExtra("selected_city_id", selectedCityID + "");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    @Override
    protected void onDestroy() {
        try {
            Utils.psLog("Clearing Objects on Destroy");

            mRecyclerView.addOnItemTouchListener(null);
            collapsingToolbar = null;
            toolbar = null;
            detailImage.setImageResource(0);
            detailImage = null;
            mLayoutManager = null;
            categoryRowDataList.clear();
            mAdapter = null;
            categoryRowDataList = null;
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
     * Start Block - Init UI Functions
     * *------------------------------------------------------------------------------------------------
     */
    private void initUI() {
        initToolbar();
        initCollapsingToolbarLayout();

        mainLayout = findViewById(R.id.coordinator_layout);

        p = new Picasso.Builder(this).build();
        // For Memory Control
        //.memoryCache(new LruCache(1))
        //.build();
    }

    private void initCollapsingToolbarLayout() {
        try {
            collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initCollapsingToolbarLayout.", e);
        }
    }

    private void initToolbar() {
        try {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            //if(Utils.isAndroid_5_0()){
            //    Utils.setMargins(toolbar, 0, -102, 0, 0);
            //}
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setTitle("");
            toolbar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        } catch (Resources.NotFoundException e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }

    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     * *------------------------------------------------------------------------------------------------
     */
    private void initData() {
        try {
            detailImage = findViewById(R.id.detail_image);
            pCity = GlobalData.citydata;
            selectedCityID = pCity.id;
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initData.", e);
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     * *------------------------------------------------------------------------------------------------
     */
    private void bindData() {
        try {
            if (collapsingToolbar != null) {

                collapsingToolbar.setTitle(Utils.getSpannableString(this, pCity.name));
                makeCollapsingToolbarLayoutLooksGood(collapsingToolbar);

            }

//            p.load(Config.APP_IMAGES_URL + pCity.cover_image_file)
//                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
//                    .into(detailImage);

            Utils.bindImage(this, p, detailImage, pCity.cover_image_file, 1);

            detailImage.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));

        } catch (Exception e) {
            Utils.psErrorLogE("Error in bindData.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     * *------------------------------------------------------------------------------------------------
     */
    public void loadCategoryGrid() {
        try {
            mRecyclerView = findViewById(R.id.my_recycler_view);

            mRecyclerView.setHasFixedSize(true);

            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new CategoryAdapter(this, categoryRowDataList, mRecyclerView, p);
            mRecyclerView.setAdapter(mAdapter);

            for (PCategoryData cd : pCity.categories) {
                CategoryRowData info = new CategoryRowData();
                info.setCatName(cd.name);
                info.setCatImage(cd.cover_image_file);
                categoryRowDataList.add(info);
            }

            mAdapter.notifyItemInserted(categoryRowDataList.size());

            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    onItemClicked(position);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));

            mRecyclerView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        } catch (Resources.NotFoundException e) {
            Utils.psErrorLogE("Error in loadCategoryGrid.", e);
        }
    }

    public void onItemClicked(int position) {
        final Intent intent;
        intent = new Intent(this, SubCategoryActivity.class);
        intent.putExtra("selected_category_index", position);
        intent.putExtra("selected_city_id", selectedCityID);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/


    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     * *------------------------------------------------------------------------------------------------
     */
    private void makeCollapsingToolbarLayoutLooksGood(CollapsingToolbarLayout collapsingToolbarLayout) {
        try {
            final Field field = collapsingToolbarLayout.getClass().getDeclaredField("mCollapsingTextHelper");
            field.setAccessible(true);

            final Object object = field.get(collapsingToolbarLayout);
            final Field tpf = object.getClass().getDeclaredField("mTextPaint");
            tpf.setAccessible(true);

            ((TextPaint) tpf.get(object)).setTypeface(Utils.getTypeFace(this, Utils.Fonts.ROBOTO));
            ((TextPaint) tpf.get(object)).setColor(ContextCompat.getColor(this, R.color.colorAccent));
        } catch (Exception ignored) {
        }
    }

    private void saveSelectedCityInfo(PCityData ct) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("_id", ct.id);
            editor.putString("_name", ct.name);
            editor.putString("_cover_image", ct.cover_image_file);
            editor.putString("_address", ct.address);
            editor.putString("_city_region_lat", ct.lat);
            editor.putString("_city_region_lng", ct.lng);
            editor.apply();
        } catch (Exception e) {
            Utils.psErrorLogE("Error in saveSelectedCityInfo.", e);
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/
}
