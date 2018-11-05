package com.ngoucoorp.cameroonguide.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.ngoucoorp.cameroonguide.Config;
import com.ngoucoorp.cameroonguide.GlobalData;
import com.ngoucoorp.cameroonguide.R;
import com.ngoucoorp.cameroonguide.activities.GalleryActivity;
import com.ngoucoorp.cameroonguide.models.PNewsData;
import com.ngoucoorp.cameroonguide.utilities.Utils;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;


import java.lang.reflect.Type;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */
public class NotificationFragment extends Fragment {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/
    private View view;
    private SharedPreferences pref;
    ProgressDialog prgDialog;
    private CollapsingToolbarLayout collapsingToolbar;
    private Bundle bundle;
    private Intent intent;
    private Picasso p;




    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(getActivity() != null) {
            pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        }
        view = inflater.inflate(R.layout.fragment_notification, container, false);

        initData();

        initUI();

        return view;
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/
    private void initData() {
        try {
            Gson gson = new Gson();
            Type newsType = new TypeToken<PNewsData>() {
            }.getType();
            GlobalData.notifData = gson.fromJson(pref.getString("notif_data",""), newsType);

        } catch (Exception e) {
            Utils.psErrorLogE("Error in init data.", e);
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/
    private void initUI() {



        TextView txtMessage = view.findViewById(R.id.latest_push_message);

        txtMessage.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));


        if (!pref.getString("_push_noti_message", "").equals("")) {
            txtMessage.setText(pref.getString("_push_noti_message", ""));
        } else {
            txtMessage.setText(" N.A ");
        }

        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

        try {
            p = new Picasso.Builder(getContext())
                    .memoryCache(new LruCache(1))
                    .build();

        } catch (Exception e) {
            Utils.psErrorLogE("Error in initData.", e);
        }



        ImageView newsImage = view.findViewById(R.id.news_image);
//            p.load(Config.APP_IMAGES_URL + newsData.images.get(0).path)
//                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
//                    .into(newsImage);

        if(GlobalData.notifData!=null)
        if(GlobalData.notifData.images!=null)
        Utils.bindImage(getActivity(), p, newsImage, GlobalData.notifData.images.get(0), 1);

        newsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.psLog("Open Gallery");
                bundle = new Bundle();
                bundle.putParcelable("images", GlobalData.notifData);
                bundle.putString("from", "news");

                intent = new Intent(getContext(), GalleryActivity.class);
                intent.putExtra("images_bundle", bundle);
                startActivity(intent);

            }
        });

        try {
            collapsingToolbar = view.findViewById(R.id.collapsing_toolbar);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initCollapsingToolbarLayout.", e);
        }


        if (collapsingToolbar != null) {
            collapsingToolbar.setTitle(GlobalData.notifData.title);
        }

        MobileAds.initialize(getContext(), getResources().getString(R.string.banner_ad_unit_id));
        if (Config.SHOW_APMOB) {
            AdView mAdView = view.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            AdView mAdView = view.findViewById(R.id.adView);
            mAdView.setVisibility(View.GONE);
        }


        TextView newsDescription = view.findViewById(R.id.news_description);
        newsDescription.setText(GlobalData.notifData.description);

        com.rey.material.widget.TextView phone = view.findViewById(R.id.phone_event);
        phone.setText(GlobalData.notifData.phone);
        phone.setTypeface(Utils.getTypeFace(getContext(), Utils.Fonts.ROBOTO));
        phone.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCallPermissionGranted()){
                    call_action();
                }
                //    doPhoneCall(view);
            }
        });

        Utils.psLog("phone_event "+GlobalData.notifData.phone);

        com.rey.material.widget.TextView date_time = view.findViewById(R.id.date_time);
        date_time.setText(GlobalData.notifData.date_event + " , "+ GlobalData.notifData.time_event+" "+GlobalData.notifData.time_format);


        com.rey.material.widget.TextView website = view.findViewById(R.id.website);
        website.setText(GlobalData.notifData.website_event);


    }



    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/


    public boolean isCallPermissionGranted(){
        if(Build.VERSION.SDK_INT >=23){
            if(ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
                return true;
            }else{
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE},1);
                return false;
            }
        }else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[],int[] grantResults){
        switch (requestCode){
            case 1 :{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    call_action();
                }else{
                    Toast.makeText(getActivity().getBaseContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void call_action(){
        try {
            Utils.psLog("Calling Phone : " + GlobalData.notifData.phone);
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + GlobalData.notifData.phone));
            startActivity(intent);
        } catch (SecurityException se) {
            Utils.psErrorLog("Error in calling phone. ", se);
        }
    }


}
