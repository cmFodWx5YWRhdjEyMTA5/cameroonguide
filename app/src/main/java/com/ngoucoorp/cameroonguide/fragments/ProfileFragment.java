package com.ngoucoorp.cameroonguide.fragments;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngoucoorp.cameroonguide.R;
import com.ngoucoorp.cameroonguide.utilities.Utils;

import java.io.File;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */

public class ProfileFragment extends Fragment {

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     * *------------------------------------------------------------------------------------------------
     */

    private SharedPreferences pref;
    private ImageView imgProfilePhoto;
    private TextView tvUserName;
    private TextView tvEmail;
    private TextView tvAboutMe;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initUI(view);

        bindData();

        return view;
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/
    private void initUI(View view) {
        imgProfilePhoto = view.findViewById(R.id.iv_profile_photo);
        tvUserName = view.findViewById(R.id.tv_name);

        tvEmail = view.findViewById(R.id.tv_email);

        tvAboutMe = view.findViewById(R.id.tv_about_me);

        if(getContext() != null) {
            tvAboutMe.setTypeface(Utils.getTypeFace(getContext(), Utils.Fonts.ROBOTO));
            tvEmail.setTypeface(Utils.getTypeFace(getContext(), Utils.Fonts.ROBOTO));
            tvUserName.setTypeface(Utils.getTypeFace(getContext(), Utils.Fonts.ROBOTO));
        }

        imgProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
        tvUserName.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
        tvEmail.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
        tvAboutMe.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/
    public void bindData() {
        try {

            if(getActivity() != null) {
                pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
            }

            tvUserName.setText(pref.getString("_login_user_name", ""));
            tvEmail.setText(pref.getString("_login_user_email", ""));

            if (pref.getString("_login_user_about_me", "").equals("")) {
                tvAboutMe.setVisibility(View.GONE);
            } else {
                tvAboutMe.setVisibility(View.VISIBLE);
                tvAboutMe.setText(pref.getString("_login_user_about_me", ""));
            }

            if(!pref.getString("_login_user_photo", "").equals("")) {

                File file ;

                ContextWrapper cw = new ContextWrapper(Utils.activity.getApplicationContext());
                File directory = cw.getDir("imageDir", Context.MODE_APPEND);
                file = new File(directory, pref.getString("_login_user_photo", ""));

                //file = new File(Environment.getExternalStorageDirectory() + "/" + pref.getString("_login_user_photo", ""));

                if (file.exists()) {

                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    imgProfilePhoto.setImageBitmap(myBitmap);
                } else {
                    Drawable myDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_person_black);
                    imgProfilePhoto.setImageDrawable(myDrawable);
                }

            }else {
                Drawable myDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_person_black);
                imgProfilePhoto.setImageDrawable(myDrawable);
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in bind data.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/
}





