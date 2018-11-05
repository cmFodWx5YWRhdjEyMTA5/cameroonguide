package com.ngoucoorp.cameroonguide.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.ngoucoorp.cameroonguide.GlobalData;
import com.ngoucoorp.cameroonguide.R;
import com.ngoucoorp.cameroonguide.activities.SelectedCityActivity;
import com.ngoucoorp.cameroonguide.adapters.CityAdapter;
import com.ngoucoorp.cameroonguide.models.PCityData;
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

public class CitiesListFragment extends Fragment {

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------
    private RecyclerView mRecyclerView;
    private ProgressWheel progressWheel;
    private CityAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView display_message;
    private ArrayList<PCityData> pCityDataList;
    private ArrayList<PCityData> pCityDataSet;
    private NestedScrollView singleLayout;
    private TextView scCityName;
    private TextView scCityLocation;
    private TextView scCityAbout;
    private TextView scCityCatCount;
    private TextView scCitySubCatCount;
    private TextView scCityItemCount;
    private ImageView scCityPhoto;
    private FrameLayout mainLayout;
    private String jsonStatusSuccessString;
    private String connectionError;
    private Picasso p;
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Public Variables
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Constructor
    //-------------------------------------------------------------------------------------------------------------------------------------
    public CitiesListFragment() {

    }
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Constructor
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cities_list, container, false);

        initUI(view);

        initData();

        return view;
    }

    @Override
    public void onDestroy() {

        try {
            mRecyclerView = null;

            progressWheel = null;
            swipeRefreshLayout = null;
            //p.shutdown();
            Utils.unbindDrawables(mainLayout);
            GlobalData.citydata = null;
            super.onDestroy();
        }catch (Exception e){
            super.onDestroy();
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init UI Function
    //-------------------------------------------------------------------------------------------------------------------------------------

    private void initUI(View view){

        mainLayout = view.findViewById(R.id.cities_layout);

        if(getContext() != null) {
            p = new Picasso.Builder(getContext()).build();
        }
        // For memory control
        //.memoryCache(new LruCache(1))
        //.build();

        initSingleUI(view);

        initSwipeRefreshLayout(view);

        initProgressWheel(view);

        initRecyclerView(view);


    }

    private void initSingleUI(View view) {

        singleLayout =view.findViewById(R.id.single_city_layout);
        scCityName = view.findViewById(R.id.sc_city_name);
        scCityLocation = view.findViewById(R.id.sc_city_loc);
        scCityAbout = view.findViewById(R.id.sc_city_desc);
        scCityCatCount = view.findViewById(R.id.txt_cat_count);
        scCitySubCatCount = view.findViewById(R.id.txt_sub_cat_count);
        scCityItemCount = view.findViewById(R.id.txt_item_count);
        scCityPhoto = view.findViewById(R.id.sc_city_photo);
        Button scCityExplore = view.findViewById(R.id.button_explore);

        int screenWidth = Utils.getScreenWidth(getContext());

        int rlWidth = (screenWidth/3) - 20;

        RelativeLayout r1 =  view.findViewById(R.id.rl_count1);
        RelativeLayout r2 =  view.findViewById(R.id.rl_count2);
        RelativeLayout r3 =  view.findViewById(R.id.rl_count3);

        r1.setMinimumWidth(rlWidth);
        r2.setMinimumWidth(rlWidth);
        r3.setMinimumWidth(rlWidth);

        scCityPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                try {

                    if (pCityDataList != null && pCityDataList.size() > 0) {
                        final Intent intent;
                        intent = new Intent(getActivity(), SelectedCityActivity.class);
                        GlobalData.citydata = pCityDataList.get(0);
                        intent.putExtra("selected_city_id", pCityDataList.get(0).id);

                        if (getActivity() != null) {
                            getActivity().startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                        }
                    }
                }catch (Exception e) {
                    Utils.psErrorLog("onClick", e);
                }

            }
        });

        scCityExplore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                try {
                    if (pCityDataList != null && pCityDataList.size() > 0) {

                        final Intent intent;
                        intent = new Intent(getActivity(), SelectedCityActivity.class);
                        GlobalData.citydata = pCityDataList.get(0);
                        intent.putExtra("selected_city_id", pCityDataList.get(0).id);

                        if (getActivity() != null) {
                            getActivity().startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                        }
                    }

                }catch (Exception e) {
                    Utils.psErrorLog("onClick", e);
                }
            }
        });



    }

    private void initSwipeRefreshLayout(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                try {
                    if (pCityDataList != null) {
                        pCityDataList.clear();
                    }
                    requestData(Config.APP_API_URL + Config.GET_ALL);
                }catch (Exception e) {
                    Utils.psErrorLog("onRefresh", e);
                }
            }
        });
    }

    private void initProgressWheel(View view) {
        progressWheel = view.findViewById(R.id.progress_wheel);
    }

    private void initRecyclerView(View view) {

        try {
            mRecyclerView = view.findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(llm);
            display_message = view.findViewById(R.id.display_message);
            display_message.setVisibility(View.GONE);

            pCityDataSet = new ArrayList<>();
            adapter = new CityAdapter(getActivity(), pCityDataSet, this.p);
            mRecyclerView.setAdapter(adapter);

            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    onItemClicked(position);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        }catch (Exception e) {
            Utils.psErrorLog("initRecyclerView", e);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Init UI Function
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init Data Function
    //-------------------------------------------------------------------------------------------------------------------------------------

    private void initData(){

        if(pCityDataList != null) {
            pCityDataList.clear();
        }

        requestData(Config.APP_API_URL + Config.GET_ALL);

        jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
        connectionError = getResources().getString(R.string.connection_error);

    }

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
                        if (status.equals(jsonStatusSuccessString)) {

                            if(progressWheel != null ) {
                                progressWheel.setVisibility(View.GONE);
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<PCityData>>() {
                                }.getType();

                                pCityDataList = gson.fromJson(jsonObject.getString("data"), listType);


                                if (pCityDataList != null && pCityDataList.size() > 1) {
                                    singleLayout.setVisibility(View.GONE);
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    updateDisplay();
                                } else {
                                    mRecyclerView.setVisibility(View.GONE);
                                    singleLayout.setVisibility(View.VISIBLE);
                                    stopLoading();
                                    updateSingleDisplay();
                                }

                                updateGlobalCityList();
                            }

                        } else {
                            stopLoading();
                            Utils.psLog("Error in loading CityList.");
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

                        if(progressWheel != null) {
                            progressWheel.setVisibility(View.GONE);
                        }

                        if(display_message != null) {
                            display_message.setVisibility(View.VISIBLE);

                            // a modifier pour afficher un pop up d'erreur de reseau
                            new AlertDialog.Builder(getContext())
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("May you are not connected to internet ")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            getActivity().finish();
                                            System.exit(0);
                                        }
                                    })
                                    .show();
                          //  display_message.setText(connectionError);
                        }
                    } catch (Exception e) {
                        Utils.psErrorLogE("onErrorResponse", e);
                    }
                }
            });

            cacheRequest.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VolleySingleton.getInstance(getActivity()).addToRequestQueue(cacheRequest);
        }catch (Exception e) {
            Utils.psErrorLog("requestData", e);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Init Data Function
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Bind Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void updateSingleDisplay() {
        try {

            if (pCityDataList != null && pCityDataList.size() > 0) {

                display_message.setVisibility(View.GONE);
                singleLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
                scCityName.setText(pCityDataList.get(0).name);
                scCityLocation.setText(pCityDataList.get(0).address);
                scCityAbout.setText(pCityDataList.get(0).description);

                String cityCatCountStr =  pCityDataList.get(0).category_count + " Categories";
                scCityCatCount.setText(cityCatCountStr);

                String citySubCatCount = pCityDataList.get(0).sub_category_count + " Sub Categories";
                scCitySubCatCount.setText(citySubCatCount);

                String cityItemCount = pCityDataList.get(0).item_count + " Items";
                scCityItemCount.setText(cityItemCount);

//                p.load(Config.APP_IMAGES_URL + pCityDataList.get(0).cover_image_file)
//                        .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
//                        .into(scCityPhoto);

                Utils.bindImage(getContext(), p, scCityPhoto, pCityDataList.get(0).cover_image_file, 1 );

            }
        }catch(Exception e){
            Utils.psErrorLogE("Error in single display data binding.", e);
        }
    }

    private void updateGlobalCityList() {
        GlobalData.cityDatas.clear();

        if(pCityDataList != null) {
            GlobalData.cityDatas.addAll(pCityDataList);
        }
    }

    private void updateDisplay() {

        try {
            if (swipeRefreshLayout.isRefreshing()) {
                pCityDataSet.clear();
                adapter.notifyDataSetChanged();

                pCityDataSet.addAll(pCityDataList);
            } else {
                pCityDataSet.addAll(pCityDataList);
            }
            stopLoading();

            if (pCityDataSet != null) {
                adapter.notifyItemInserted(pCityDataSet.size());
            }
        }catch (Exception e) {
            Utils.psErrorLog("updateDisplay", e);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Bind Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    private void onItemClicked(int position) {
        try {
            Utils.psLog("Position : " + position);
            Intent intent;
            intent = new Intent(getActivity(), SelectedCityActivity.class);
            GlobalData.citydata = pCityDataList.get(position);
            intent.putExtra("selected_city_id", pCityDataList.get(position).id);

            if (getActivity() != null) {
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
            }
        }catch (Exception e) {
            Utils.psErrorLog("onItemClicked", e);
        }
    }


    private void stopLoading(){
        try {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }catch (Exception e){
            Utils.psErrorLog("stopLoading", e);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------


}
