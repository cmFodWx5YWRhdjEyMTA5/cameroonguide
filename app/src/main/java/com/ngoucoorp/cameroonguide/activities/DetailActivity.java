package com.ngoucoorp.cameroonguide.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngoucoorp.cameroonguide.models.PItemData;
import com.ngoucoorp.cameroonguide.models.PReviewData;
import com.ngoucoorp.cameroonguide.Config;
import com.ngoucoorp.cameroonguide.GlobalData;
import com.ngoucoorp.cameroonguide.R;
import com.ngoucoorp.cameroonguide.utilities.CacheRequest;
import com.ngoucoorp.cameroonguide.utilities.Utils;
import com.ngoucoorp.cameroonguide.utilities.VolleySingleton;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */

public class DetailActivity extends AppCompatActivity {

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------
    private Toolbar toolbar;
    private ImageView detailImage;
    private ArrayList<PReviewData> itemReviewData;
    private MapView mMapView;
    private SharedPreferences pref;
    private TextView txtLikeCount;
    private TextView txtReviewCount;
    private TextView txtTotalReview;
    private TextView txtReviewMessage;
    private TextView txtNameTime;
    private TextView txtAddress;
    private TextView txtPhone;
    private TextView txtEmail;
    private TextView txtWebsite;
    private TextView txtDescription;
    private TextView title;
    private ImageView userPhoto;
    private Button btnLike;
    private Button btnMoreReview;
    private Button btnInquiry;
    private FloatingActionButton fab;
    private int selectedItemId;
    private int selectedCityId;
    private Bundle bundle;
    private Intent intent;
    private Boolean isFavourite = false;
    private RatingBar getRatingBar;
    private RatingBar setRatingBar;
    private TextView ratingCount;
    private Animation animation;
    private String jsonStatusSuccessString;
    private Picasso p;
    private CoordinatorLayout mainLayout;
    private NestedScrollView nsv;
    private Button btnReview;
    private Button btnShare;

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        initData();

        initUI(savedInstanceState);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Utils.psLog("OnActivityResult");
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                bindData();
            }
        }
    }

    @Override
    public void onBackPressed() {
        try {
            Intent in = new Intent();
            in.putExtra("selected_item_id", GlobalData.itemData.id);
            in.putExtra("like_count", GlobalData.itemData.like_count);
            in.putExtra("review_count", GlobalData.itemData.review_count);
            setResult(RESULT_OK, in);

            GlobalData.itemData = null;

            finish();
            overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in BackPress.", e);
            finish();

        }

    }

    @Override
    public void onDestroy() {
        try {
            toolbar = null;
            detailImage = null;
            mMapView.onDestroy();
            mMapView = null;
            pref = null;
            txtLikeCount = null;
            txtReviewCount = null;
            txtTotalReview = null;
            txtReviewMessage = null;
            txtNameTime = null;
            txtAddress = null;
            txtPhone = null;
            txtEmail = null;
            txtWebsite=null;
            txtDescription = null;
            title = null;
            userPhoto = null;
            btnLike = null;
            btnMoreReview = null;
            btnInquiry = null;
            fab = null;
            bundle = null;
            intent = null;
            getRatingBar = null;
            setRatingBar = null;
            ratingCount = null;
            animation = null;
            btnReview = null;
            btnShare = null;

            //p.shutdown();

            Utils.unbindDrawables(mainLayout);
            mainLayout = null;
            GlobalData.itemData = null;

            super.onDestroy();
        } catch (Exception e) {
            super.onDestroy();
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void initData() {

        try {
            p = new Picasso.Builder(this)
                    .memoryCache(new LruCache(1))
                    .build();

            pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

            selectedItemId = getIntent().getIntExtra("selected_item_id", 0);
            selectedCityId = getIntent().getIntExtra("selected_city_id", 0);
            requestData(Config.APP_API_URL + Config.ITEMS_BY_ID + selectedItemId + "/city_id/" + selectedCityId);
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);

            updateTouchCount(selectedItemId);
        } catch (Exception e) {
            Utils.psErrorLog("initData", e);
        }
    }

    private void updateTouchCount(int selectedItemId) {
        try {
            final String URL = Config.APP_API_URL + Config.POST_TOUCH_COUNT + selectedItemId;
            Utils.psLog(URL);
            HashMap<String, String> params = new HashMap<>();
            params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
            params.put("city_id", selectedCityId + "");
            doSubmit(URL, params, "touch");
        } catch (Exception e) {
            Utils.psErrorLogE("Error in Touch Count.", e);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion init Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void initUI(Bundle savedInstanceState) {

        try {


            mainLayout = findViewById(R.id.coordinator_layout);

            nsv = findViewById(R.id.nsv);

            initToolbar();

            btnLike = findViewById(R.id.btn_like);
            btnLike.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            btnReview = findViewById(R.id.btn_review);
            btnReview.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            btnShare = findViewById(R.id.btn_share);
            btnShare.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            txtLikeCount = findViewById(R.id.total_like_count);
            txtLikeCount.setTypeface(Utils.getTypeFace(this, Utils.Fonts.ROBOTO));
            txtLikeCount.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            txtReviewCount = findViewById(R.id.total_review_count);
            txtReviewCount.setTypeface(Utils.getTypeFace(this, Utils.Fonts.ROBOTO));
            txtReviewCount.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            txtTotalReview = findViewById(R.id.total_review);
            txtTotalReview.setTypeface(Utils.getTypeFace(this, Utils.Fonts.ROBOTO));
            txtTotalReview.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            txtReviewMessage = findViewById(R.id.review_message);
            txtReviewMessage.setTypeface(Utils.getTypeFace(this, Utils.Fonts.ROBOTO));
            txtReviewMessage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            txtNameTime = findViewById(R.id.name_time);
            txtNameTime.setTypeface(Utils.getTypeFace(this, Utils.Fonts.ROBOTO));
            txtNameTime.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            txtAddress = findViewById(R.id.address);
            txtAddress.setTypeface(Utils.getTypeFace(this, Utils.Fonts.ROBOTO));
            txtAddress.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            txtPhone = findViewById(R.id.phone);
            txtPhone.setTypeface(Utils.getTypeFace(this, Utils.Fonts.ROBOTO));
            txtPhone.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            txtPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isCallPermissionGranted()){
                        call_action();
                    }
                //    doPhoneCall(view);
                }
            });

            txtEmail = findViewById(R.id.mail);
            txtEmail.setTypeface(Utils.getTypeFace(this, Utils.Fonts.ROBOTO));
            txtEmail.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            txtWebsite = findViewById(R.id.website);
            txtWebsite.setTypeface(Utils.getTypeFace(this, Utils.Fonts.ROBOTO));
            txtWebsite.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            txtWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("http://"+txtWebsite.getText().toString()));
                    startActivity(intent);
                }
            });

            txtDescription = findViewById(R.id.txtDescription);
            txtDescription.setTypeface(Utils.getTypeFace(this, Utils.Fonts.ROBOTO));
            txtDescription.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            title = findViewById(R.id.title);
            title.setTypeface(Utils.getTypeFace(this, Utils.Fonts.ROBOTO));
            title.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            userPhoto = findViewById(R.id.user_photo);
            userPhoto.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            detailImage = findViewById(R.id.detail_image);
            detailImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            btnMoreReview = findViewById(R.id.btn_more_review);
            btnMoreReview.setTypeface(Utils.getTypeFace(this, Utils.Fonts.ROBOTO));
            btnMoreReview.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            btnInquiry = findViewById(R.id.btn_inquiry);
            btnInquiry.setTypeface(Utils.getTypeFace(this, Utils.Fonts.ROBOTO));
            btnInquiry.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            getRatingBar = findViewById(R.id.get_rating);
            setRatingBar = findViewById(R.id.set_rating);
            ratingCount = findViewById(R.id.rating_count);
            animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.pop_out);


            fab = findViewById(R.id.fab);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doFavourite(v);

                    Utils.psLog("Start Animation.");


                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                            try {
                                Utils.psLog("Started Animation.");
                                if (isFavourite) {
                                    isFavourite = false;
                                    fab.setImageResource(R.drawable.ic_favorite_border);
                                } else {
                                    isFavourite = true;
                                    fab.setImageResource(R.drawable.ic_favorite_white);
                                }
                            } catch (Exception e) {
                                Utils.psErrorLog("onAnimationStart", e);
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }


                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    fab.clearAnimation();
                    fab.startAnimation(animation);
                }
            });

            btnLike.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    doLike(v);

                    Animation rotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in);
                    btnLike.startAnimation(rotate);
                    rotate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            });

            getRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                    try {
                        if (pref.getInt("_login_user_id", 0) != 0) {
                            ratingChanged(rating);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.login_required,
                                    Toast.LENGTH_LONG).show();
                            getRatingBar.setRating(0);
                        }
                    } catch (Exception e) {
                        Utils.psErrorLog("onRatingChanged", e);
                    }
                }
            });


            initializeMap(savedInstanceState);

            MobileAds.initialize(this, getResources().getString(R.string.app_ad_id));
            if (Config.SHOW_APMOB) {
                AdView mAdView = findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);

                AdView mAdView2 = findViewById(R.id.adView2);
                AdRequest adRequest2 = new AdRequest.Builder().build();
                mAdView2.loadAd(adRequest2);
            } else {
                AdView mAdView = findViewById(R.id.adView);
                mAdView.setVisibility(View.GONE);
                AdView mAdView2 = findViewById(R.id.adView2);
                mAdView2.setVisibility(View.GONE);
            }

            btnLike.requestFocus();

        } catch (Exception e) {
            Utils.psErrorLogE("Error in Init UI.", e);
        }

    }

    public boolean isCallPermissionGranted(){
        if(Build.VERSION.SDK_INT >=23){
            if(checkSelfPermission(Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
                return true;
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},1);
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
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void call_action(){
        try {
            Utils.psLog("Calling Phone : " + txtPhone.getText().toString());
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + txtPhone.getText().toString()));
            startActivity(intent);
        } catch (SecurityException se) {
            Utils.psErrorLog("Error in calling phone. ", se);
        }
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
        } catch (Exception e) {
            Utils.psErrorLog("initToolbar", e);
        }

    }

    private void initializeMap(Bundle savedInstanceState) {
        if (Utils.isGooglePlayServicesOK(this)) {
            mMapView = findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();
            try {
                MapsInitializer.initialize(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Bind Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void bindData() {
        try {
            bindTitle();
            bindToolbarImage();
            bindCountValues();
            bindReview();
            bindWebsite();
            bindDescription();
            bindShopInfo();
            bindFavourite(fab);
            bindLike(txtLikeCount);
            bindRate();

        } catch (Exception e) {
            Utils.psErrorLogE("Error in binding", e);
        }
    }

    private void bindTitle() {
        try {
            title.setTypeface(Utils.getTypeFace(this, Utils.Fonts.ROBOTO));
            title.setText(GlobalData.itemData.name);
        } catch (Exception e) {
            Utils.psErrorLog("Error in bind Title .", e);
        }
    }

    private void bindToolbarImage() {
        try {
            detailImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGallery();
                }
            });

            // Version 1
//            p.load(Config.APP_IMAGES_URL + GlobalData.itemData.images.get(0).path)
//                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
//                    .into(detailImage);

            Utils.bindImage(this, p, detailImage, GlobalData.itemData.images.get(0), 1);

        } catch (Exception e) {
            Utils.psErrorLogE("Error in Bind Toolbar Image.", e);
        }
    }

    private void bindCountValues() {
        try {
            if (GlobalData.itemData != null) {
                String likeCountStr = " " + GlobalData.itemData.like_count + " ";
                txtLikeCount.setText(likeCountStr);

                String reviewCountStr = " " + GlobalData.itemData.review_count + " ";
                txtReviewCount.setText(reviewCountStr);
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in Bind Count.", e);
        }
    }

    private void bindReview() {
        try {
            itemReviewData = GlobalData.itemData.reviews;

            txtNameTime.setVisibility(View.VISIBLE);
            txtReviewMessage.setVisibility(View.VISIBLE);
            btnMoreReview.setText(getString(R.string.view_more_review));

            if (itemReviewData != null) {
                if (itemReviewData.size() > 0) {
                    if (itemReviewData.size() == 1) {
                        String totalReviewStr = itemReviewData.size() + " " + getString(R.string.review);
                        txtTotalReview.setText(totalReviewStr);
                    } else {
                        String totalReviewStr = itemReviewData.size() + " " + getString(R.string.reviews);
                        txtTotalReview.setText(totalReviewStr);
                    }

                    if (itemReviewData != null) {
                        PReviewData reviewData = itemReviewData.get(0);
                        String nameTimeStr = reviewData.appuser_name + " " + "(" + reviewData.added + ")";
                        txtNameTime.setText(nameTimeStr);
                        txtReviewMessage.setText(reviewData.review);
                        if (!reviewData.profile_photo.equals("")) {
                            Utils.psLog(" Loading User photo : " + Config.APP_IMAGES_URL + reviewData.profile_photo);
                            p.load(Config.APP_IMAGES_URL + reviewData.profile_photo).resize(150, 150).into(userPhoto);
                        } else {
                            userPhoto.setColorFilter(Color.argb(114, 114, 114, 114));
                        }

                    }


                } else {
                    txtTotalReview.setText(getString(R.string.no_review_count));
                    txtNameTime.setVisibility(View.GONE);
                    txtReviewMessage.setVisibility(View.GONE);
                    btnMoreReview.setText(getString(R.string.add_first_review));

                    Drawable myDrawable = ContextCompat.getDrawable(this, R.drawable.ic_rate_review_black);

                    userPhoto.setImageDrawable(myDrawable);

                }
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in Bind Reviews.", e);
        }
    }

    private void bindWebsite() {
        try {
            txtWebsite.setText(GlobalData.itemData.website);
        } catch (Exception e) {
            Utils.psErrorLogE("error in binding website", e);
        }
    }


    private void bindDescription() {
        try {
            txtDescription.setText(GlobalData.itemData.description);
        } catch (Exception e) {
            Utils.psErrorLogE("error in binding description", e);
        }
    }

    private void bindShopInfo() {

        txtAddress.setText(GlobalData.itemData.address);
        txtPhone.setText(GlobalData.itemData.phone);
        txtEmail.setText(GlobalData.itemData.email);

        try {
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    googleMap.getUiSettings().setZoomControlsEnabled(false);


                    double latitude = Double.parseDouble(GlobalData.itemData.lat);
                    double longitude = Double.parseDouble(GlobalData.itemData.lng);

                    MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(GlobalData.itemData.name);
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(
                            new LatLng(latitude, longitude)).zoom(15.1f).build();

                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {

                            nsv.scrollTo(0, 10);
                            btnLike.requestFocus();
                            Utils.psLog("Finish Camera update.");
                        }

                        @Override
                        public void onCancel() {
                            nsv.scrollTo(0, 10);
                            btnLike.requestFocus();
                            Utils.psLog("Cancel Camera update.");
                        }
                    });


                    googleMap.addMarker(marker);
                }
            });

            btnLike.requestFocus();
            nsv.scrollTo(0, 10);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in map initialize.", e);
        }

    }


    private void bindFavourite(FloatingActionButton fab) {
        try {
            if (pref.getInt("_login_user_id", 0) != 0) {
                final String URL = Config.APP_API_URL + Config.GET_FAVOURITE + GlobalData.itemData.id;
                Utils.psLog(URL);
                HashMap<String, String> params = new HashMap<>();
                params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
                params.put("city_id", selectedCityId + "");
                getFavourite(URL, params, fab);
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in Bind Favourite.", e);
        }
    }

    public void bindLike(View view) {
        try {
            if (pref.getInt("_login_user_id", 0) != 0) {
                final String URL = Config.APP_API_URL + Config.GET_LIKE + GlobalData.itemData.id;
                Utils.psLog(URL);
                HashMap<String, String> params = new HashMap<>();
                params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
                params.put("city_id", selectedCityId + "");
                params.put("platformName", "android");
                getLike(URL, params);
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in Bind Like. ", e);
        }
    }

    public void bindRate() {
        try {
            final String URL = Config.APP_API_URL + Config.POST_ITEM_IS_RATE + GlobalData.itemData.id;
            Utils.psLog(URL);
            HashMap<String, String> params = new HashMap<>();
            params.put("city_id", selectedCityId + "");
            getRate(URL, params);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in bind Rating", e);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Bind Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void getFavourite(String postURL, HashMap<String, String> params, final FloatingActionButton fab) {

        try {
            JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String success_status = response.getString("status");
                                String data_status = response.getString("data");
                                if (success_status.equals(jsonStatusSuccessString)) {
                                    if (data_status.equals("yes")) {
                                        isFavourite = true;
                                        fab.setImageResource(R.drawable.ic_favorite_white);
                                    }
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

            req.setShouldCache(false);
            VolleySingleton.getInstance(this).addToRequestQueue(req);

        } catch (Exception e) {
            Utils.psErrorLog("getFavourite", e);
        }

    }

    private void getLike(String postURL, HashMap<String, String> params) {
        try {
            JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String success_status = response.getString("status");

                                if (success_status.equals(jsonStatusSuccessString)) {
                                    txtLikeCount.setText(response.getString("total"));
                                    btnLike.setBackgroundResource(R.drawable.ic_done);
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

            req.setShouldCache(false);
            VolleySingleton.getInstance(this).addToRequestQueue(req);
        } catch (Exception e) {
            Utils.psErrorLog("getLike", e);
        }
    }

    private void requestData(String uri) {
        try {
            Utils.psLog("Item Detail " + uri);

            CacheRequest cacheRequest = new CacheRequest(0, uri, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    try {
                        final String jsonString = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers));
                        JSONObject jsonObject = new JSONObject(jsonString);

                        String status = jsonObject.getString("status");
                        if (status.equals(jsonStatusSuccessString)) {

                            Gson gson = new Gson();
                            Type listType = new TypeToken<PItemData>() {
                            }.getType();
                            GlobalData.itemData = gson.fromJson(jsonObject.getString("data"), listType);

                            if (GlobalData.itemData != null) {
                                bindData();
                            }

                        } else {
                            Utils.psLog("Error in Item Detail loading.");
                        }

                    } catch (UnsupportedEncodingException | JSONException e) {

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

            VolleySingleton.getInstance(this).addToRequestQueue(cacheRequest);
        } catch (Exception e) {
            Utils.psErrorLog("requestData", e);
        }
    }

    private void openGallery() {

        try {
            bundle = new Bundle();
            bundle.putParcelable("images", GlobalData.itemData);
            bundle.putString("from", "item");

            intent = new Intent(getApplicationContext(), GalleryActivity.class);
            intent.putExtra("images_bundle", bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
        } catch (Exception e) {
            Utils.psErrorLog("openGallery", e);
        }
    }

    private void getRate(String postURL, HashMap<String, String> params) {
        try {
            JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String success_status = response.getString("status");
                                String data_status = response.getString("data");

                                if (success_status.equals(jsonStatusSuccessString)) {
                                    JSONObject jresponse = new JSONObject(data_status);
                                    if (jresponse.getString("isRate").equals("yes")) {
                                        setRatingBar.setRating(Float.parseFloat(jresponse.getString("total")));
                                        if (Float.parseFloat(jresponse.getString("total")) != 0.0) {
                                            String tmpRatingCount = "Total Rating : " + jresponse.getString("total");
                                            ratingCount.setText(tmpRatingCount);
                                        }
                                    } else {
                                        ratingCount.setText(getString(R.string.first_rating));
                                    }

                                }


                            } catch (Exception e) {
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

            req.setShouldCache(false);
            VolleySingleton.getInstance(this).addToRequestQueue(req);
        } catch (Exception e) {
            Utils.psErrorLog("getRate", e);
        }
    }

    private void doSubmit(String postURL, HashMap<String, String> params, final String fromWhere) {
        try {
            JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String status = response.getString("status");
                                if (status.equals(jsonStatusSuccessString)) {

                                    if (fromWhere.equals("like")) {
                                        Utils.psLog("Count From Server : " + response.getString("data"));
                                        GlobalData.itemData.like_count = response.getString("data");
                                        String likeCount = " " + GlobalData.itemData.like_count + " ";
                                        txtLikeCount.setText(likeCount);
                                    }
                                } else {
                                    showFailPopup();
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

            req.setShouldCache(false);
            VolleySingleton.getInstance(this).addToRequestQueue(req);
        } catch (Exception e) {
            Utils.psErrorLog("doSubmit", e);
        }
    }

    private void showFailPopup() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.sorry_title);
        builder.setMessage(R.string.like_fail);
        builder.setPositiveButton(R.string.OK, null);
        builder.show();
    }

    private void showRatingFailPopup() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.sorry_title);
        builder.setMessage(R.string.rating_fail);
        builder.setPositiveButton(R.string.OK, null);
        builder.show();
    }

    private void ratingChanged(float rating) {

        try {
            Utils.psLog(String.valueOf(rating));
            if (isInternetOn()) {

                final String URL = Config.APP_API_URL + Config.POST_ITEM_RATING + selectedItemId;
                Utils.psLog(URL);
                HashMap<String, String> params = new HashMap<>();
                params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
                params.put("rating", String.valueOf(rating));
                params.put("city_id", selectedCityId + "");

                JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String success_status = response.getString("status");

                                    if (success_status.equals(jsonStatusSuccessString)) {
                                        setRatingBar.setRating(Float.parseFloat(response.getString("data")));
                                        String tmpRatingCount = "Total Rating : " + response.getString("data");
                                        ratingCount.setText(tmpRatingCount);
                                    } else {
                                        showRatingFailPopup();
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

                req.setShouldCache(false);
                VolleySingleton.getInstance(this).addToRequestQueue(req);

            } else {
                showOffline();
            }
        } catch (Exception e) {
            Utils.psErrorLog("ratingChanged", e);
        }

    }

    private void showOffline() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.sorry_title);
        builder.setMessage(R.string.device_offline);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.psLog("OK clicked.");
            }
        });
        builder.show();
    }

    private void showNeedLogin() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.sorry_title);
        builder.setMessage(R.string.login_required);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
                startActivity(intent);
                Utils.psLog("OK clicked.");
            }
        });
        builder.show();
    }

    // Method to share either text or URL.
    private void shareTextUrl() {

        try {
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");

            // Add data to the intent, the receiving app will decide
            // what to do with it.
            share.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);

            share.putExtra(Intent.EXTRA_TEXT, R.string.app_url);

            startActivity(Intent.createChooser(share, "Share link!"));
        } catch (Exception e) {
            Utils.psErrorLog("shareTextUrl", e);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Public Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    public void doPhoneCall(View view) {

        try {
            Utils.psLog("Calling Phone : " + txtPhone.getText().toString());
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + txtPhone.getText().toString()));
            startActivity(intent);
        } catch (SecurityException se) {
            Utils.psErrorLog("Error in calling phone. ", se);
        }

    }

    public void doEmail(View view) {
        try {
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{pref.getString("_email", "")});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hello");
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (Exception e) {
            Utils.psErrorLog("doEmail", e);
        }
    }

    public void doInquiry(View view) {
        try {
            final Intent intent;
            intent = new Intent(this, InquiryActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
        } catch (Exception e) {
            Utils.psErrorLog("doInquiry", e);
        }
    }

    public void doReview(View view) {
        try {
            if (itemReviewData != null) {
                if (itemReviewData.size() > 0) {
                    Intent intent = new Intent(this, ReviewListActivity.class);
                    intent.putExtra("selected_item_id", selectedItemId);
                    intent.putExtra("selected_city_id", selectedCityId);
                    startActivityForResult(intent, 1);
                    overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                } else {
                    if (pref.getInt("_login_user_id", 0) != 0) {
                        Intent intent = new Intent(this, ReviewEntryActivity.class);
                        intent.putExtra("selected_item_id", selectedItemId);
                        intent.putExtra("selected_city_id", selectedCityId);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                    } else {
                        Intent intent = new Intent(this, UserLoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                    }
                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("doReview", e);
        }
    }

    public void doFavourite(View view) {

        try {
            if (isInternetOn()) {

                if (pref.getInt("_login_user_id", 0) != 0) {
                    final String URL = Config.APP_API_URL + Config.POST_ITEM_FAVOURITE + GlobalData.itemData.id;
                    Utils.psLog(URL);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
                    params.put("city_id", selectedCityId + "");
                    params.put("platformName", "android");
                    doSubmit(URL, params, "favourite");
                } else {
                    if (isFavourite) {
                        isFavourite = false;
                        fab.setImageResource(R.drawable.ic_favorite_border);
                    } else {
                        isFavourite = true;
                        fab.setImageResource(R.drawable.ic_favorite_white);
                    }
                    showNeedLogin();
                }

            } else {
                showOffline();
            }
        } catch (Exception e) {
            Utils.psErrorLog("doFavourite", e);
        }
    }

    public void doLike(View view) {

        try {
            if (isInternetOn()) {

                if (pref.getInt("_login_user_id", 0) != 0) {
                    final String URL = Config.APP_API_URL + Config.POST_ITEM_LIKE + GlobalData.itemData.id;
                    Utils.psLog(URL);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
                    params.put("city_id", selectedCityId + "");
                    params.put("platformName", "android");
                    doSubmit(URL, params, "like");
                } else {
                    showNeedLogin();
                }


            } else {
                showOffline();
            }
        } catch (Exception e) {
            Utils.psErrorLog("doLike", e);
        }
    }

    public void doShare(View view) {

        shareTextUrl();

    }


    public final boolean isInternetOn() {

        try {
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) { // connected to the internet
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        // connected to wifi
                        return true;
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        // connected to the mobile provider's data plan
                        return true;
                    }
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("isInternetOn", e);
        }
        return false;
    }


    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Public Functions
    //-------------------------------------------------------------------------------------------------------------------------------------


}