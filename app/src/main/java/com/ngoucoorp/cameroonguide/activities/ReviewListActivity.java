package com.ngoucoorp.cameroonguide.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import android.widget.ListView;

import com.ngoucoorp.cameroonguide.GlobalData;
import com.ngoucoorp.cameroonguide.R;
import com.ngoucoorp.cameroonguide.adapters.ReviewAdapter;
import com.ngoucoorp.cameroonguide.utilities.Utils;
import com.squareup.picasso.Picasso;


/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */

public class ReviewListActivity extends AppCompatActivity {

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     * *------------------------------------------------------------------------------------------------
     */


    private ListView listView;
    private Toolbar toolbar;
    private int selectedItemId;
    private int selectedCityId;
    private SharedPreferences pref;
    private SpannableString reviewListString;
    private Picasso p;

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
        setContentView(R.layout.activity_review_list);

        initData();

        initUI();

        bindData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                bindData();
            }
        }
        Utils.psLog(" Result : " + requestCode + " : " + resultCode);
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent();
        setResult(RESULT_OK, in);
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);

    }

    @Override
    public void onDestroy() {

        try {
            toolbar = null;

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
        initFAB();
        initList();
    }

    private void initList() {
        try {
            listView = findViewById(R.id.review_list);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initList.", e);
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
            toolbar.setTitle(reviewListString);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }
    }

    private void initFAB() {
        try {
            FloatingActionButton fab = findViewById(R.id.fab_new_review);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pref.getInt("_login_user_id", 0) != 0) {
                        Intent intent = new Intent(getApplicationContext(), ReviewEntryActivity.class);
                        intent.putExtra("selected_item_id", selectedItemId);
                        intent.putExtra("selected_city_id", selectedCityId);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);

                    } else {
                        Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
                        startActivity(intent);
                    }

                }
            });
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initFAB.", e);
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
            pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            selectedItemId = getIntent().getIntExtra("selected_item_id", 0);
            selectedCityId = getIntent().getIntExtra("selected_city_id", 0);
            reviewListString = Utils.getSpannableString(this, getString(R.string.review_list));

            p = new Picasso.Builder(this).build();
            // FOr memory control
//            .memoryCache(new LruCache(1))
//            .build();

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
            ReviewAdapter adapter = new ReviewAdapter(this, GlobalData.itemData.reviews, p);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in bindData.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

}
