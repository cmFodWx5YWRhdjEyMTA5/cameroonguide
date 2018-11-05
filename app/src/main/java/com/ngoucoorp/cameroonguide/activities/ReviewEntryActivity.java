package com.ngoucoorp.cameroonguide.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngoucoorp.cameroonguide.models.PItemData;
import com.ngoucoorp.cameroonguide.Config;
import com.ngoucoorp.cameroonguide.GlobalData;
import com.ngoucoorp.cameroonguide.R;
import com.ngoucoorp.cameroonguide.utilities.Utils;
import com.ngoucoorp.cameroonguide.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;


/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */

public class ReviewEntryActivity extends AppCompatActivity {
    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     * *------------------------------------------------------------------------------------------------
     */

    private Toolbar toolbar;
    private SharedPreferences pref;
    private EditText txtReviewMessage;
    private ProgressBar pb;
    private String jsonStatusSuccessString;
    private SpannableString reviewString;
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
        setContentView(R.layout.activity_review_entry);

        initData();

        initUI();

        bindData();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    @Override
    public void onDestroy() {

        try {
            toolbar = null;
            pref = null;

            Utils.unbindDrawables(mainLayout);
            mainLayout = null;

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
        mainLayout = findViewById(R.id.coordinator_layout);
        mainLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
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

            toolbar.setTitle(reviewString);
        } catch (Exception e) {
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
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);


            reviewString = Utils.getSpannableString(this, getString(R.string.review));

            pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        } catch (Exception e) {
            Utils.psErrorLogE("Error in init data.", e);
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
            TextView txtUserName = findViewById(R.id.login_user_name);
            TextView txtUserEmail = findViewById(R.id.login_user_email);

            txtUserName.setText(pref.getString("_login_user_name", ""));
            txtUserEmail.setText(pref.getString("_login_user_email", ""));
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

    public void doReview(View view) {
        try {
            if (inputValidation()) {
                pb = findViewById(R.id.loading_spinner);
                pb.setVisibility(View.VISIBLE);

                Bundle bundle = getIntent().getExtras();

                if (bundle != null) {
                    final String URL = Config.APP_API_URL + Config.POST_REVIEW + bundle.getInt("selected_item_id");
                    txtReviewMessage = findViewById(R.id.input_review_message);

                    HashMap<String, String> params = new HashMap<>();
                    params.put("review", txtReviewMessage.getText().toString().trim());
                    params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
                    params.put("city_id", String.valueOf(pref.getInt("_id", 0)));

                    doSubmit(URL, params);
                }
            }
        }catch (Exception e) {
            Utils.psErrorLog("doReview", e);
        }

    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     * *------------------------------------------------------------------------------------------------
     */

    private void doSubmit(String postURL, HashMap<String, String> params) {
        if (isInternetOn()) {
            JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String success_status = response.getString("status");

                                if (success_status.equals(jsonStatusSuccessString)) {
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<PItemData>() {
                                    }.getType();
                                    GlobalData.itemData = gson.fromJson(response.getString("data"), listType);

                                    pb = findViewById(R.id.loading_spinner);
                                    pb.setVisibility(View.GONE);

                                    Utils.psLog(success_status);
                                    Utils.psLog(" --- Need to refresh review list and count --- ");
                                    //showSuccessPopup();
                                    Intent in = new Intent();
                                    setResult(RESULT_OK, in);
                                    finish();
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
            req.setShouldCache(false);
            // add the request object to the queue to be executed
            VolleySingleton.getInstance(this).addToRequestQueue(req);
        } else {
            showOffline();
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

    private boolean inputValidation() {
        txtReviewMessage = findViewById(R.id.input_review_message);

        if (txtReviewMessage.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), R.string.review_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;

    }

    public final boolean isInternetOn() {

        try {
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(cm != null ) {
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
        }catch (Exception e) {
            Utils.psErrorLog("isInternetOn", e);
        }
        return false;
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

}
