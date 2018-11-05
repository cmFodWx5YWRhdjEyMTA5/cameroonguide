package com.ngoucoorp.cameroonguide.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngoucoorp.cameroonguide.listeners.ClickListener;
import com.ngoucoorp.cameroonguide.listeners.RecyclerTouchListener;
import com.ngoucoorp.cameroonguide.Config;
import com.ngoucoorp.cameroonguide.R;
import com.ngoucoorp.cameroonguide.activities.DetailActivity;
import com.ngoucoorp.cameroonguide.adapters.ItemAdapter;
import com.ngoucoorp.cameroonguide.models.PItemData;
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
public class FavouritesListFragment extends Fragment {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private List<PItemData> items;
    private List<PItemData> myDataset;
    private ItemAdapter mAdapter;
    private ProgressWheel progressWheel;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private TextView displayMessage;
    private SharedPreferences pref;
    private String noDataAvaiString;
    private String jsonStatusSuccessString;
    private Picasso p;
    private LinearLayout mainLayout;
    private int currentSize = 0;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favourites_list, container, false);

        initData("home");

        initUI(view);

        return view;
    }


    @Override
    public void onDestroy() {

        try {
            mRecyclerView = null;
            mLayoutManager = null;
            mAdapter = null;
            progressWheel = null;
            myDataset = null;
            //p.shutdown();
            Utils.unbindDrawables(mainLayout);

            super.onDestroy();
        }catch (Exception e){
            super.onDestroy();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        initData("back");

    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - init Data Functions
     **------------------------------------------------------------------------------------------------*/
    private void initData(String fromWhere) {

        if(getActivity() != null) {
            p = new Picasso.Builder(getActivity()).build();
        }
        // For memory control
        //.memoryCache(new LruCache(1))
        //.build();
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        Utils.psLog(Config.APP_API_URL + Config.GET_FAVOURITE_ITEMS + pref.getInt("_login_user_id", 0) + "/count/" + Config.PAGINATION + "/form/0");
        //requestData(Config.APP_API_URL + Config.GET_FAVOURITE_ITEMS + pref.getInt("_login_user_id", 0) + "/count/" + Config.PAGINATION + "/form/0");
        requestData(Config.APP_API_URL + Config.GET_FAVOURITE_ITEMS + pref.getInt("_login_user_id", 0) + "/count/" + Config.PAGINATION + "/from/0", fromWhere);


        try {
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            noDataAvaiString = getResources().getString(R.string.no_data_available);

        }catch(Exception e){
            Utils.psErrorLogE("Error in init data.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    private void initUI(View view){
        initProgressWheel(view);
        initRecyclerView(view);

        mainLayout = view.findViewById(R.id.main_layout);
        displayMessage = view.findViewById(R.id.display_message);
        displayMessage.setVisibility(View.GONE);

        if(getActivity() != null) {
            MobileAds.initialize(getActivity(), getActivity().getResources().getString(R.string.banner_ad_unit_id));
            if (Config.SHOW_APMOB) {
                AdView mAdView = view.findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            } else {
                AdView mAdView = view.findViewById(R.id.adView);
                mAdView.setVisibility(View.GONE);
            }
        }
    }

    private void initProgressWheel(View view) {
        progressWheel = view.findViewById(R.id.progress_wheel);
    }

    private void initRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        myDataset = new ArrayList<>();

        mAdapter = new ItemAdapter(getActivity(), myDataset, mRecyclerView, p);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnLoadMoreListener(new ItemAdapter.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                //add progress item

                if(myDataset != null) {
                    int from = myDataset.size();
                    if (currentSize != from) {
                        currentSize = from;
                        myDataset.add(null);
                        mAdapter.notifyItemInserted(myDataset.size() - 1);
                        Utils.psLog(Config.APP_API_URL + Config.GET_FAVOURITE_ITEMS + pref.getInt("_login_user_id", 0) + "/count/" + Config.PAGINATION + "/from/" + from);
                        requestData(Config.APP_API_URL + Config.GET_FAVOURITE_ITEMS + pref.getInt("_login_user_id", 0) + "/count/" + Config.PAGINATION + "/from/" + from, "home");
                    }

                }
            }

        });

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

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    private void requestData(String uri, final String fromWhere) {

        /*
        JsonObjectRequest request = new JsonObjectRequest(uri,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {

                                if(myDataset != null) {


                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<PItemData>>() {
                                    }.getType();
                                    items = gson.fromJson(response.getString("data"), listType);

                                    if(items != null) {

                                        if(!fromWhere.equals("home")) {
                                            myDataset.clear();
                                            mAdapter.notifyDataSetChanged();
                                        }

                                        if (myDataset.size() > 0) {
                                            myDataset.remove(myDataset.size() - 1);
                                            mAdapter.notifyItemRemoved(myDataset.size());
                                        }

                                        Utils.psLog("Count : " + items.size());

                                        progressWheel.setVisibility(View.GONE);
                                        for (PItemData pItem : items) {
                                            myDataset.add(pItem);
                                        }
                                        mAdapter.notifyItemInserted(myDataset.size());
                                    }
                                }
                                mAdapter.setLoaded();
                            }else{

                                if(myDataset != null) {
                                    if (myDataset.size() > 0) {
                                        myDataset.remove(myDataset.size() - 1);
                                        mAdapter.notifyItemRemoved(myDataset.size());
                                    }
                                }

                                mAdapter.setLoaded();
                                progressWheel.setVisibility(View.GONE);

                                if(myDataset == null && myDataset.size()<=0) {
                                    displayMessage.setVisibility(View.VISIBLE);
                                    displayMessage.setText(noDataAvaiString);
                                }
                            }


                        } catch (JSONException e) {
                            displayMessage.setVisibility(View.VISIBLE);
                            displayMessage.setText(noDataAvaiString);

                        } catch (Exception ee){
                            Utils.psErrorLog("error", ee);
                        }


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        Utils.psLog("error");
                    }
                });

        request.setShouldCache(false);
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
        */

        try {
            CacheRequest cacheRequest = new CacheRequest(0, uri, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    try {
                        final String jsonString = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers));
                        JSONObject jsonObject = new JSONObject(jsonString);

                        String status = jsonObject.getString("status");
                        if (status.equals(jsonStatusSuccessString)) {

                            if (myDataset != null) {


                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<PItemData>>() {
                                }.getType();
                                items = gson.fromJson(jsonObject.getString("data"), listType);

                                if (items != null) {

                                    if (!fromWhere.equals("home")) {
                                        myDataset.clear();
                                        mAdapter.notifyDataSetChanged();
                                    }

                                    if (myDataset.size() > 0) {
                                        myDataset.remove(myDataset.size() - 1);
                                        mAdapter.notifyItemRemoved(myDataset.size());
                                    }

                                    Utils.psLog("Count : " + items.size());

                                    progressWheel.setVisibility(View.GONE);
                                    myDataset.addAll(items);
                                    mAdapter.notifyItemInserted(myDataset.size());
                                }
                            }
                            mAdapter.setLoaded();

                        } else {

                            if (myDataset != null) {
                                if (myDataset.size() > 0) {
                                    myDataset.remove(myDataset.size() - 1);
                                    mAdapter.notifyItemRemoved(myDataset.size());
                                }
                            }

                            mAdapter.setLoaded();
                            progressWheel.setVisibility(View.GONE);

                            if (myDataset == null && myDataset.size() <= 0) {
                                displayMessage.setVisibility(View.VISIBLE);
                                displayMessage.setText(noDataAvaiString);
                            }

                        }

                    } catch (UnsupportedEncodingException | JSONException e) {

                        e.printStackTrace();
                    } catch (Exception e) {
                        Utils.psErrorLog("Error in loading", e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        displayMessage.setVisibility(View.VISIBLE);
                        displayMessage.setText(noDataAvaiString);
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

        }catch (Exception e) {
            Utils.psErrorLog("requestData",e);
        }
    }

    private void onItemClicked(int position) {

        try {
            final Intent intent;
            intent = new Intent(getActivity(), DetailActivity.class);
            Utils.psLog("Selected City ID : " + myDataset.get(position).city_id);
            intent.putExtra("selected_item_id", myDataset.get(position).id);
            intent.putExtra("selected_city_id", myDataset.get(position).city_id);

            if (getActivity() != null) {
                getActivity().startActivityForResult(intent, 0);
            }
        }catch (Exception e) {
            Utils.psErrorLog("onItemClicked", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/


}
