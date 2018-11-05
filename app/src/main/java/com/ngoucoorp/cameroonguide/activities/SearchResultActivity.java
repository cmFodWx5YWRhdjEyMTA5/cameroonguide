package com.ngoucoorp.cameroonguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngoucoorp.cameroonguide.listeners.ClickListener;
import com.ngoucoorp.cameroonguide.listeners.RecyclerTouchListener;
import com.ngoucoorp.cameroonguide.models.PItemData;
import com.ngoucoorp.cameroonguide.Config;
import com.ngoucoorp.cameroonguide.R;
import com.ngoucoorp.cameroonguide.adapters.ItemAdapter;
import com.ngoucoorp.cameroonguide.utilities.Utils;
import com.ngoucoorp.cameroonguide.utilities.VolleySingleton;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */

public class SearchResultActivity extends AppCompatActivity {

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     * *------------------------------------------------------------------------------------------------
     */

    private Toolbar toolbar;
    private StaggeredGridLayoutManager mLayoutManager;
    private ItemAdapter mAdapter;
    private List<PItemData> myDataset;
    private List<PItemData> it;
    private String jsonStatusSuccessString;
    private SpannableString searchResultString;
    private Picasso p = null;
    private CoordinatorLayout mainLayout;

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
        setContentView(R.layout.activity_search_result);

        initData();

        initUI();

        bindData();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
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
        initRecyclerView();

        mainLayout = findViewById(R.id.coordinator_layout);
    }

    private void initToolbar() {
        try {
            toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setTitle(searchResultString);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in init initToolbar.", e);
        }
    }

    public void initRecyclerView() {
        try {
            RecyclerView mRecyclerView = findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLayoutManager);

            myDataset = new ArrayList<>();

            mAdapter = new ItemAdapter(this, myDataset, mRecyclerView, p);
            mRecyclerView.setAdapter(mAdapter);


            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    onItemClicked(position);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        } catch (Exception e) {
            Utils.psErrorLogE("Error in init initRecyclerView.", e);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            Utils.psLog("Clearing Objects on Destroy");

            toolbar = null;

            mLayoutManager = null;

            myDataset.clear();

            mAdapter = null;
            myDataset = null;

            Utils.unbindDrawables(mainLayout);

            super.onDestroy();
        } catch (Exception e) {
            super.onDestroy();
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

            p = new Picasso.Builder(this)
                    .memoryCache(new LruCache(1))
                    .build();
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            searchResultString = Utils.getSpannableString(this, getString(R.string.search_result));
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
            TextView txtSelectedCity = findViewById(R.id.search_city);
            txtSelectedCity.setText(getIntent().getStringExtra("selected_city_name"));
            TextView txtSearchKeyword = findViewById(R.id.search_keyword);
            txtSearchKeyword.setText(getIntent().getStringExtra("search_keyword"));

            final String URL = Config.APP_API_URL + Config.POST_ITEM_SEARCH + getIntent().getStringExtra("selected_city_id");
            Utils.psLog(URL);
            doSearch(URL, getIntent().getStringExtra("search_keyword"));
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
    public void doSearch(final String URL, final String keyword) {

        HashMap<String, String> params = new HashMap<>();
        params.put("keyword", keyword);

        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {

                                if (myDataset != null) {
                                    if (myDataset.size() > 0) {
                                        myDataset.remove(myDataset.size() - 1);
                                        mAdapter.notifyItemRemoved(myDataset.size());
                                    }
                                }

                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<PItemData>>() {
                                }.getType();
                                it = gson.fromJson(response.getString("data"), listType);

                                myDataset.addAll(it);

                                if (myDataset != null) {
                                    mAdapter.notifyItemInserted(myDataset.size());
                                }

                                mAdapter.setLoaded();

                            } else {

                                Utils.psLog("Error in loading.");
                            }
                        } catch (JSONException e) {

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
        sr.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(sr);
    }

    public void onItemClicked(int position) {
        final Intent intent;
        intent = new Intent(getApplicationContext(), DetailActivity.class);
        intent.putExtra("selected_item_id", myDataset.get(position).id);
        intent.putExtra("selected_city_id", myDataset.get(position).city_id);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

}