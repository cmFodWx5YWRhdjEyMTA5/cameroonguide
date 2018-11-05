package com.ngoucoorp.cameroonguide.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ngoucoorp.cameroonguide.fragments.AboutFragment;
import com.ngoucoorp.cameroonguide.fragments.CitiesListFragment;
import com.ngoucoorp.cameroonguide.fragments.FavouritesListFragment;
import com.ngoucoorp.cameroonguide.fragments.NotificationFragment;
import com.ngoucoorp.cameroonguide.fragments.ProfileFragment;
import com.ngoucoorp.cameroonguide.fragments.SearchFragment;
import com.ngoucoorp.cameroonguide.fragments.UserForgotPasswordFragment;
import com.ngoucoorp.cameroonguide.fragments.UserLoginFragment;
import com.ngoucoorp.cameroonguide.fragments.UserRegisterFragment;
import com.ngoucoorp.cameroonguide.Config;
import com.ngoucoorp.cameroonguide.R;
import com.ngoucoorp.cameroonguide.utilities.Utils;
import com.ngoucoorp.cameroonguide.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by N'gou Coorp
 * Contact Email :ngounoubosseloic@gmail.com
 */

public class MainActivity extends AppCompatActivity {

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private & Public Variables
     * *------------------------------------------------------------------------------------------------
     */
    private Toolbar toolbar = null;
    private ActionBarDrawerToggle drawerToggle = null;
    private DrawerLayout drawerLayout = null;
    private NavigationView navigationView = null;
    private int currentMenuId = 0;
    private FABActions fabActions;
    private SharedPreferences pref;
    private FloatingActionButton fab;
    private SpannableString appNameString;
    private SpannableString profileString;
    private SpannableString registerString;
    private SpannableString forgotPasswordString;
    private SpannableString searchKeywordString;
    private SpannableString favouriteItemString;
    public Fragment fragment = null;
    private SpannableString aboutString;






    private String regId = "";
    RequestParams params = new RequestParams();
    ProgressDialog prgDialog;
    private String serviceNotAvaiString;
    private String jsonStatusSuccessString;
    private String gcmRegisterSuccessString;
    private String gcmUnregisterSuccessString;
    private String gcmCannotConnectString;
    private String gcmSomethingWrongString;
    private String gcmRegisterNotSuccessString;
    private String gcmRequestNotFoundString;
    /*------------------------------------------------------------------------------------------------
     * End Block - Private & PublicVariables
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     * *------------------------------------------------------------------------------------------------
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUtils();

        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if(pref.contains("_registered_fcm")) {
          //  Toast.makeText(this, "You are already registered for push", Toast.LENGTH_SHORT).show();
        }else{

            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            serviceNotAvaiString = getResources().getString(R.string.service_not_available);
            gcmRegisterSuccessString = getResources().getString(R.string.gcm_register_success);
            gcmUnregisterSuccessString = getResources().getString(R.string.gcm_unregister_success);
            gcmRegisterNotSuccessString = getResources().getString(R.string.gcm_register_not_success);
            gcmRequestNotFoundString = getResources().getString(R.string.request_not_found);
            gcmSomethingWrongString = getResources().getString(R.string.something_wrong);
            gcmCannotConnectString = getResources().getString(R.string.cannot_connect);
            Toast.makeText(this, "You must register for push", Toast.LENGTH_SHORT).show();
            try{
                getTokenInBackground("reg");
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        initUI();

        initData();

        bindData();

        FirebaseMessaging.getInstance().subscribeToTopic("cameroonguide");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            Utils.psLog("OnActivityResult");

            if (requestCode == 1) {

                if (resultCode == RESULT_OK) {
                    refreshProfileData();
                }
            } else if (requestCode == 0) { // for refresh favourite list

                Utils.psLog("Inside 0");
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in main.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Init Utils Class
     * *------------------------------------------------------------------------------------------------
     */

    private void initUtils() {
        new Utils(this);
        VolleySingleton.getInstance(this);
        FirebaseAnalytics.getInstance(this);
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init Utils Class
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     * *------------------------------------------------------------------------------------------------
     */

    private void initUI() {
        initToolbar();
        initDrawerLayout();
        initNavigationView();
        initFAB();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (getSupportActionBar() != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void initDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout != null && toolbar != null) {
            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                }
            };

            drawerLayout.addDrawerListener(drawerToggle);
            drawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    drawerToggle.syncState();
                }
            });
        }
    }

    private void initNavigationView() {
        navigationView = findViewById(R.id.nav_view);

        if (navigationView != null) {

            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                            navigationMenuChanged(menuItem);
                            return true;
                        }
                    });
        }
    }

    private void initFAB() {
        fab = findViewById(R.id.fab);
        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClicked(view);
            }
        });
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
            pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            if (getIntent().getBooleanExtra("show_noti", false)) {
                savePushMessage(getIntent().getStringExtra("msg"));
                openFragment(R.id.nav_push_noti);
            } else {
                openFragment(R.id.nav_home);
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in getting notification flag data.", e);
        }

        try {
            appNameString = Utils.getSpannableString(this, getString(R.string.app_name));
            profileString = Utils.getSpannableString(this, getString(R.string.profile));
            registerString = Utils.getSpannableString(this, getString(R.string.register));
            forgotPasswordString = Utils.getSpannableString(this, getString(R.string.forgot_password));
            searchKeywordString = Utils.getSpannableString(this, getString(R.string.search_keyword));
            favouriteItemString = Utils.getSpannableString(this, getString(R.string.favourite_item));
            aboutString = Utils.getSpannableString(this, getString(R.string.about_app));

        } catch (Exception e) {
            Utils.psErrorLogE("Error in init Data.", e);
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

        toolbar.setTitle(appNameString);

        bindMenu();

    }

    // This function will change the menu based on the user is logged in or not.
    public void bindMenu() {
        if (pref.getInt("_login_user_id", 0) != 0) {
            navigationView.getMenu().setGroupVisible(R.id.group_after_login, true);
            navigationView.getMenu().setGroupVisible(R.id.group_before_login, false);
        } else {
            navigationView.getMenu().setGroupVisible(R.id.group_before_login, true);
            navigationView.getMenu().setGroupVisible(R.id.group_after_login, false);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     * *------------------------------------------------------------------------------------------------
     */

    private void disableFAB() {
        fab.setVisibility(View.GONE);
    }

    private void enableFAB() {
        fab.setVisibility(View.VISIBLE);
    }

    private void updateFABIcon(int icon) {
        fab.setImageResource(icon);
    }

    private void updateFABAction(FABActions action) {
        fabActions = action;
    }

    private void navigationMenuChanged(MenuItem menuItem) {
        openFragment(menuItem.getItemId());
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
    }

    private void updateFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    private void doLogout() {
        pref.edit().remove("_login_user_id").apply();
        pref.edit().remove("_login_user_name").apply();
        pref.edit().remove("_login_user_email").apply();
        pref.edit().remove("_login_user_about_me").apply();
        pref.edit().remove("_login_user_photo").apply();

        bindMenu();

        openFragment(R.id.nav_home);
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     * *------------------------------------------------------------------------------------------------
     */

    public void fabClicked(View view) {
        if (fabActions == FABActions.PROFILE) {
            final Intent intent;
            intent = new Intent(this, EditProfileActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    public void openFragment(int menuId) {

        switch (menuId) {
            case R.id.nav_home:
            case R.id.nav_home_login:
                disableFAB();
                fragment = new CitiesListFragment();
                toolbar.setTitle(appNameString);
                break;

            case R.id.nav_profile:
            case R.id.nav_profile_login:
                if (pref.getInt("_login_user_id", 0) != 0) {
                    enableFAB();
                    updateFABIcon(R.drawable.ic_edit_white);
                    updateFABAction(FABActions.PROFILE);
                    fragment = new ProfileFragment();
                } else {
                    fragment = new UserLoginFragment();
                }
                toolbar.setTitle(profileString);
                break;

            case R.id.nav_register:
                fragment = new UserRegisterFragment();
                toolbar.setTitle(registerString);
                break;

            case R.id.nav_forgot:
                fragment = new UserForgotPasswordFragment();
                toolbar.setTitle(forgotPasswordString);
                break;

            case R.id.nav_logout:
                doLogout();
                break;

            case R.id.nav_search_keyword:
            case R.id.nav_search_keyword_login:
                disableFAB();
                fragment = new SearchFragment();
                toolbar.setTitle(searchKeywordString);
                break;

            case R.id.nav_push_noti:
            case R.id.nav_push_noti_login:
                disableFAB();
                fragment = new NotificationFragment();
                break;

            case R.id.nav_favourite_item_login:
                disableFAB();
                fragment = new FavouritesListFragment();
                toolbar.setTitle(favouriteItemString);
                break;

            case R.id.nav_about:
            case R.id.nav_about_login:
                fragment = new AboutFragment();
                toolbar.setTitle(aboutString);
                break;

            default:
                break;
        }

        if (currentMenuId != menuId && menuId != R.id.nav_logout) {
            currentMenuId = menuId;

            updateFragment(fragment);

            try {
                navigationView.getMenu().findItem(menuId).setChecked(true);
            } catch (Exception e) {
                Utils.psErrorLog("Error in Find Item. " + menuId, e);
            }
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage("Do you really want to quit?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                            System.exit(0);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }
        return true;
    }

    // Neet to check
    public void refreshProfileData() {

        if (fragment instanceof ProfileFragment) {
            ((ProfileFragment) fragment).bindData();
        }
    }

    public void refreshProfile() {
        openFragment(R.id.nav_profile_login);
    }

    public void refreshNotification() {
        try {
            fragment = new NotificationFragment();

            updateFragment(fragment);
            if (pref.getInt("_login_user_id", 0) != 0) {
                currentMenuId = R.id.nav_push_noti_login;
            } else {
                currentMenuId = R.id.nav_push_noti;
            }

            navigationView.getMenu().findItem(currentMenuId).setChecked(true);
        } catch (Exception e) {
            Utils.psErrorLogE("Refresh Notification View Error. ", e);
        }

    }

    public void savePushMessage(String msg) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("_push_noti_message", msg);
        editor.apply();
    }

    public void showDownPicasso() {
//        try {
//            this.p.shutdown();
//        } catch (Exception e) {
//            Utils.psErrorLogE("Error in Shutdown picasso.", e);
//        }

    }

//    public void loadProfileImage(String path) {
//
//        if (!path.equals("")) {
//
//            p = new Picasso.Builder(this)
//                    .memoryCache(new LruCache(1))
//                    .build();
//
//            final String fileName = path;
//            Utils.psLog("file name : " + fileName);
//
//            Target target = new Target() {
//
//                @Override
//                public void onPrepareLoad(Drawable arg0) {
//                    Utils.psLog("Prepare Image to load.");
//
//                }
//
//                @Override
//                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//                    Utils.psLog("inside onBitmapLoaded ");
//
//                    try {
//                        File file;
//
//                        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//                        File directory = cw.getDir("imageDir", Context.MODE_APPEND);
//                        file = new File(directory, fileName);
//
//                        //file = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
//
//                        FileOutputStream ostream = new FileOutputStream(file);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
//                        ostream.close();
//                        Utils.psLog("Success Image Loaded.");
//
//                        refreshProfile();
//
//                        // After download finished the profile image
//                        // shutdown the Picasso threads
//                        showDownPicasso();
//
//                    } catch (Exception e) {
//                        Utils.psErrorLogE(e.getMessage(), e);
//                    }
//
//                }
//
//                @Override
//                public void onBitmapFailed(Drawable arg0) {
//                    Utils.psLog("Fail Fail Fail");
//
//                }
//            };
//
//            Utils.psLog("profile photo : " + Config.APP_IMAGES_URL + path);
//            p.load(Config.APP_IMAGES_URL + path)
//                    .resize(150, 150)
//                    .into(target);
//        }
//
//    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Enum
     * *------------------------------------------------------------------------------------------------
     */
    private enum FABActions {
        PROFILE
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Enum
     **------------------------------------------------------------------------------------------------*/




    @SuppressLint("StaticFieldLeak")
    public void getTokenInBackground(final String status) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";

                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                       regId = instanceIdResult.getToken();
                        submitToServer(status, regId);
                        Log.e("newToken",regId);
                    }


                });

                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Utils.psLog(" Msg Val " + msg);
                if (!regId.equals(""))
             //       submitToServer(status, regId);
                Utils.psLog(regId);

            }
        }.execute(null, null, null);
    }

    public void submitToServer(final String toggleStatus, String token) {


        String URL;
        if (toggleStatus.equals("reg")) {
            URL = Config.APP_API_URL + Config.POST_FCM_REGISTER;
        } else {
            URL = Config.APP_API_URL + Config.POST_FCM_UNREGISTER;
        }
        params.put("reg_id", token);
        params.put("platformName", "android");

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(URL, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {

                        Utils.psLog("Server Resp : " + response);

                        try {
                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                gcmRegisterSuccessString,
                                                Toast.LENGTH_LONG).show();


                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean("_push_noti_setting", true);
                                    editor.putBoolean("_registered_fcm", true);
                                    editor.apply();

                            } else {

                                    Toast.makeText(
                                            getApplicationContext(),
                                            gcmRegisterNotSuccessString,
                                            Toast.LENGTH_LONG).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e){
                            Utils.psErrorLog("Error in loading.", e);
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {

                            if (statusCode == 404) {
                                Toast.makeText(getApplicationContext(),
                                        gcmRequestNotFoundString,
                                        Toast.LENGTH_LONG).show();
                            } else if (statusCode == 500) {
                                Toast.makeText(getApplicationContext(),
                                        gcmSomethingWrongString,
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        gcmCannotConnectString,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                });
    }
}
