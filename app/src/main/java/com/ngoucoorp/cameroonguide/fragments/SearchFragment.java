package com.ngoucoorp.cameroonguide.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.ngoucoorp.cameroonguide.Config;
import com.ngoucoorp.cameroonguide.GlobalData;
import com.ngoucoorp.cameroonguide.R;
import com.ngoucoorp.cameroonguide.activities.SearchResultActivity;
import com.ngoucoorp.cameroonguide.listeners.SelectListener;
import com.ngoucoorp.cameroonguide.uis.PSPopupSingleSelectView;
import com.ngoucoorp.cameroonguide.utilities.Utils;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */
public class SearchFragment extends Fragment {

    private TextView txt_search;
    private int selectedCityId;
    private String selectedCityName;
    private String selectCityString;
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Constructor
    //-------------------------------------------------------------------------------------------------------------------------------------
    public SearchFragment() {

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
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initData();

        initUI(view);

        return view;
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void initUI(View view) {

        try {
            LinearLayout popupContainer = view.findViewById(R.id.choose_container);
            popupContainer.removeAllViews();

            PSPopupSingleSelectView psPopupSingleSelectView = new PSPopupSingleSelectView(getActivity(), selectCityString, GlobalData.cityDatas, "");
            psPopupSingleSelectView.setOnSelectListener(new SelectListener() {
                @Override
                public void Select(View view, int position, CharSequence text) {

                }

                @Override
                public void Select(View view, int position, CharSequence text, int id) {
                    selectedCityId = id;
                    selectedCityName = text.toString();
                }

                @Override
                public void Select(View view, int position, CharSequence text, int id, float additionalPrice) {

                }
            });
            popupContainer.addView(psPopupSingleSelectView);

            Button btn_search = view.findViewById(R.id.button_search);
            btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            prepareForSearch();
                        }
                    }
                }
            });

            txt_search = view.findViewById(R.id.input_search);

            if (getActivity() != null) {
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

            btn_search.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
            txt_search.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
        }catch (Exception e) {
            Utils.psErrorLog("initUI", e);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void initData(){
        selectCityString = getResources().getString(R.string.select_city);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Init Data
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void prepareForSearch() {

        try {
            Intent intent = new Intent(getActivity(), SearchResultActivity.class);
            intent.putExtra("selected_city_id", selectedCityId + "");
            intent.putExtra("search_keyword", txt_search.getText().toString().trim());
            intent.putExtra("selected_city_name", selectedCityName);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
            }
        }catch (Exception e) {
            Utils.psErrorLog("prepareForSearch", e);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

}