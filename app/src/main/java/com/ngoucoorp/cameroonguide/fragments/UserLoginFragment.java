package com.ngoucoorp.cameroonguide.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ngoucoorp.cameroonguide.Config;
import com.ngoucoorp.cameroonguide.GlobalData;
import com.ngoucoorp.cameroonguide.R;
import com.ngoucoorp.cameroonguide.activities.MainActivity;
import com.ngoucoorp.cameroonguide.activities.UserForgotPasswordActivity;
import com.ngoucoorp.cameroonguide.activities.UserLoginActivity;
import com.ngoucoorp.cameroonguide.activities.UserRegisterActivity;
import com.ngoucoorp.cameroonguide.utilities.Utils;
import com.ngoucoorp.cameroonguide.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */

public class UserLoginFragment extends Fragment {

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     * *------------------------------------------------------------------------------------------------
     */

    private View view;
    private EditText txtEmail;
    private EditText txtPassword;
    private ProgressDialog prgDialog;
    private String jsonStatusSuccessString;
    private LinearLayout mainLayout;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     * *------------------------------------------------------------------------------------------------
     */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_login, container, false);

        initData();

        initUI();

        return view;
    }

    @Override
    public void onDestroy() {
        try {
            Utils.unbindDrawables(mainLayout);
            GlobalData.citydata = null;
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
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);


        } catch (Exception e) {
            Utils.psErrorLogE("Error in init data.", e);
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

    private void initUI() {
        try {

            mainLayout = this.view.findViewById(R.id.main_layout);
            mainLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

            txtEmail = this.view.findViewById(R.id.input_email);

            txtPassword = this.view.findViewById(R.id.input_password);

            Button btnLogin = this.view.findViewById(R.id.button_login);

            Button btnForgot = this.view.findViewById(R.id.button_forgot);

            Button btnRegister = this.view.findViewById(R.id.button_register);

            if (getContext() != null) {
                txtPassword.setTypeface(Utils.getTypeFace(getContext(), Utils.Fonts.ROBOTO));
                btnLogin.setTypeface(Utils.getTypeFace(getContext(), Utils.Fonts.ROBOTO));
                btnForgot.setTypeface(Utils.getTypeFace(getContext(), Utils.Fonts.ROBOTO));
                btnRegister.setTypeface(Utils.getTypeFace(getContext(), Utils.Fonts.ROBOTO));
                txtEmail.setTypeface(Utils.getTypeFace(getContext(), Utils.Fonts.ROBOTO));
            }

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doLogin();
                }
            });

            btnForgot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.psLog("Forgot Click Here");
                    doForgot();
                }
            });

            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doRegister();
                }
            });

            prgDialog = new ProgressDialog(getActivity());
            prgDialog.setMessage("Please wait...");
            prgDialog.setCancelable(false);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in Init UI.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     * *------------------------------------------------------------------------------------------------
     */

    private void doLogin() {

        if (inputValidation()) {

            final String URL = Config.APP_API_URL + Config.POST_USER_LOGIN;
            Utils.psLog(URL);

            HashMap<String, String> params = new HashMap<>();
            params.put("email", txtEmail.getText().toString().trim());
            params.put("password", txtPassword.getText().toString().trim());

            doSubmit(URL, params);

        }

    }

    private void doForgot() {
        if (isInternetOn()) {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openFragment(R.id.nav_forgot);
            } else if (getActivity() instanceof UserLoginActivity) {
                startActivity(new Intent(getActivity(), UserForgotPasswordActivity.class));
            }
        } else {
            showOffline();
        }
    }

    private void doRegister() {
        if (isInternetOn()) {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openFragment(R.id.nav_register);
            } else if (getActivity() instanceof UserLoginActivity) {
                //startActivity(new Intent(getActivity(),UserRegisterActivity.class));

                Intent intent = new Intent(getActivity(), UserRegisterActivity.class);
                getActivity().startActivityForResult(intent, 0);
            }
        } else {
            showOffline();
        }
    }

    private void doSubmit(String postURL, HashMap<String, String> params) {
        if (isInternetOn()) {
            prgDialog.show();

            JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                Utils.psLog(" .... Starting User Login Callback .... " + response);

                                String status = response.getString("status");
                                if (status.equals(jsonStatusSuccessString)) {

                                    JSONObject dat = response.getJSONObject("data");
                                    String user_id = dat.getString("id");
                                    String user_name = dat.getString("username");
                                    String email = dat.getString("email");
                                    String about_me = dat.getString("about_me");
                                    //String is_banned = dat.getString("is_banned");
                                    String user_profile_photo = dat.getString("profile_photo");

                                    if (getActivity() != null) {
                                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putInt("_login_user_id", Integer.parseInt(user_id));
                                        editor.putString("_login_user_name", user_name);
                                        editor.putString("_login_user_email", email);
                                        editor.putString("_login_user_about_me", about_me);
                                        editor.putString("_login_user_photo", user_profile_photo);
                                        editor.apply();

                                        // Update Menu
                                        Utils.activity.bindMenu();

                                        if (!user_profile_photo.equals("")) {
                                            loadProfileImage(user_profile_photo);

                                        } else {
                                            prgDialog.cancel();
                                            if (getActivity() instanceof MainActivity) {
                                                ((MainActivity) getActivity()).refreshProfile();
                                            }
                                            if (getActivity() instanceof UserLoginActivity) {
                                                getActivity().finish();
                                            }
                                        }
                                    }

                                } else {
                                    Utils.psLog("Login Fail");
                                    prgDialog.cancel();
                                    showFailPopup();


                                }

                            } catch (JSONException e) {
                                prgDialog.cancel();
                                Utils.psLog("Login Fail : " + e.getMessage());
                                e.printStackTrace();
                                showFailPopup();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        prgDialog.cancel();
                        Utils.psLog("Error: " + error.getMessage());
                    }catch (Exception e) {
                        Utils.psErrorLog("onErrorResponse", e);
                    }
                }
            });
            req.setShouldCache(false);
            // add the request object to the queue to be executed
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(req);
        } else {

            showOffline();

        }

    }

    private boolean inputValidation() {

        if (txtEmail.getText().toString().equals("")) {
            if (getActivity() != null) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.email_validation_message,
                        Toast.LENGTH_LONG).show();
            }
            return false;
        }

        if (txtPassword.getText().toString().equals("")) {
            if (getActivity() != null) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.password_validation_message,
                        Toast.LENGTH_LONG).show();
            }
            return false;
        }

        return true;

    }

    private void loadProfileImage(String path) {

        if (!path.equals("")) {
            new downloadProfileImage().execute(Config.APP_IMAGES_URL + path, path);
        }

    }

    private void showFailPopup() {
        if (getActivity() != null) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.login);
            builder.setMessage(R.string.login_fail);
            builder.setPositiveButton(R.string.OK, null);
            builder.show();
        }
    }

    private void showOffline() {
        if (getActivity() != null) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
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
    }

    public final boolean isInternetOn() {

        try {
            if (getActivity() != null) {
                ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm != null) {
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    // connected to the internet
                    return activeNetwork != null && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE);
                }
            }

        } catch (Exception e) {
            Utils.psErrorLog("isInternetOn", e);
        }
        return false;
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    @SuppressLint("StaticFieldLeak")
    private class downloadProfileImage extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            URL url;
            try {
                url = new URL(params[0]);

                URLConnection conection = url.openConnection();
                conection.connect();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                File file;
                if (getActivity() != null) {
                    ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
                    File directory = cw.getDir("imageDir", Context.MODE_APPEND);
                    file = new File(directory, params[1]);

                    OutputStream output = new FileOutputStream(file.getAbsolutePath());

                    byte data[] = new byte[1024];

                    int count;
                    while ((count = input.read(data)) != -1) {

                        // writing data to file
                        output.write(data, 0, count);
                    }

                }


            } catch (Exception ee) {
                ee.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            // After download finished the profile image
            // shutdown the Picasso threads
            Utils.activity.showDownPicasso();

            prgDialog.cancel();
            if (getActivity() instanceof UserLoginActivity) {
                getActivity().finish();
            } else {
                Utils.activity.refreshProfile();
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
