package com.ngoucoorp.cameroonguide.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ngoucoorp.cameroonguide.fragments.AlertDialogRadio;
import com.ngoucoorp.cameroonguide.fragments.TabFragment;
import com.ngoucoorp.cameroonguide.models.PCategoryData;
import com.ngoucoorp.cameroonguide.models.PSubCategoryData;
import com.ngoucoorp.cameroonguide.GlobalData;
import com.ngoucoorp.cameroonguide.R;
import com.ngoucoorp.cameroonguide.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */

public class SubCategoryActivity extends AppCompatActivity implements AlertDialogRadio.AlertPositiveListener {

    private int selectedCategoryIndex = 0;
    private int selectedCityId;
    private ViewPager viewPager;
    private ArrayList<PCategoryData> categoriesList;
    private ArrayList<PSubCategoryData> subCategoriesList;
    private Adapter adapter;
    private int position = 0;
    private int selectedSortIndex = 0;



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
        setContentView(R.layout.activity_tab);

        initData();

        initUI();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab, menu);

        int i = 0;

        try {
            for (PCategoryData cd : categoriesList) {
                menu.add(0, i, 0, cd.name);
                i++;
            }

        }catch (Exception e){
            Utils.psErrorLog("onCreateOptionsMenu", e);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sorting) {
            android.app.FragmentManager manager = getFragmentManager();
            AlertDialogRadio alert = new AlertDialogRadio();
            Bundle b = new Bundle();
            b.putInt("position", position);
            alert.setArguments(b);
            alert.show(manager, "alert_dialog_radio");

        } else {
            loadCategoryUI(id);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (requestCode == 1) {

                if (resultCode == RESULT_OK) {

                    Adapter adapter = (Adapter) viewPager.getAdapter();
                    if (adapter != null) {
                        TabFragment fragment = (TabFragment) adapter.getItem(viewPager.getCurrentItem());
                        fragment.refreshLikeAndReview(data.getIntExtra("selected_item_id", 0), data.getStringExtra("like_count"), data.getStringExtra("review_count"));
                    }
                }

            }
        }catch (Exception e) {
            Utils.psErrorLog("onActivityResult", e);
        }

    }

    @Override
    protected void onDestroy() {

        try {
            adapter.mFragments.clear();
            adapter = null;
            viewPager.destroyDrawingCache();
            viewPager.removeAllViews();

            viewPager = null;

            Utils.unbindDrawables(findViewById(R.id.drawer_layout));

            super.onDestroy();
        } catch (Exception e) {
            super.onDestroy();
        }

    }


    @Override
    public void onPositiveClick(int position) {
        this.position = position;
        Utils.psLog("Selected Index " + position);
        doSorting(position);
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
        try {
            viewPager = findViewById(R.id.viewpager);
            if (viewPager != null) {
                initViewPager(viewPager, subCategoriesList);
            }
            TabLayout tabLayout = findViewById(R.id.tabs);
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setupWithViewPager(viewPager);
            updateTabFonts(tabLayout);

            initFAB();
            initToolbar();
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initUI.", e);
        }

    }

    private void initFAB() {
        try {
            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFabClicked();
                }
            });
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initFAB.", e);
        }
    }

    private void initToolbar() {
        try {
            /*
      ------------------------------------------------------------------------------------------------
      Start Block - Private Variables
      *------------------------------------------------------------------------------------------------
     */
            Toolbar toolbar = findViewById(R.id.toolbar);
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
            toolbar.setTitle(Utils.getSpannableString(this, categoriesList.get(selectedCategoryIndex).name));
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
            categoriesList = GlobalData.citydata.categories;
            selectedCategoryIndex = getIntent().getIntExtra("selected_category_index", 0);
            selectedCityId = getIntent().getIntExtra("selected_city_id", 0);
            subCategoriesList = categoriesList.get(selectedCategoryIndex).sub_categories;
            selectedCategoryIndex = getIntent().getIntExtra("selected_category_index", 0);

            selectedSortIndex = getIntent().getIntExtra("sorting_index", 0);

        } catch (Exception e) {
            Utils.psErrorLogE("Error in initData.", e);
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     * *------------------------------------------------------------------------------------------------
     */
    private void updateTabFonts(TabLayout tabLayout) {
        try {
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                TextView tt = new TextView(this);
                tt.setTypeface(Utils.getTypeFace(this, Utils.Fonts.ROBOTO));
                tt.setTextColor(Color.WHITE);

                TabLayout.Tab tab = tabLayout.getTabAt(i);

                if (tab != null) {
                    tt.setText(tab.getText());
                    tab.setCustomView(tt);
                }
            }
        }catch (Exception e) {
            Utils.psErrorLog("updateTabFonts", e);
        }
    }

    private void onFabClicked() {


        try {
            Adapter adapter = (Adapter) viewPager.getAdapter();

            if (adapter != null) {
                TabFragment fragment = (TabFragment) adapter.getItem(viewPager.getCurrentItem());
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("_selected_city_id", fragment.selectedCityID);
                editor.putInt("_selected_sub_cat_id", fragment.selectedSubCategoryID);
                editor.apply();

                final Intent intent;
                intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
            }
        }catch (Exception e) {
            Utils.psErrorLog("onFabClicked", e);
        }
    }

    private void initViewPager(ViewPager viewPager, ArrayList<PSubCategoryData> subCategoryArrayList) {
        adapter = new Adapter(getSupportFragmentManager());

        if (subCategoryArrayList != null) {
            int c_FRAGMENTS_TO_KEEP_IN_MEMORY = subCategoryArrayList.size();
            for (PSubCategoryData scd : subCategoryArrayList) {
                TabFragment tab = new TabFragment();
                tab.setData(scd, selectedCityId, selectedSortIndex);
                adapter.addFragment(tab, Utils.getSpannableString(this, scd.name) + "");

            }

            viewPager.setOffscreenPageLimit(c_FRAGMENTS_TO_KEEP_IN_MEMORY);
        }
        viewPager.setAdapter(adapter);


    }

    private void loadCategoryUI(int id) {
        Intent intent = new Intent(this, SubCategoryActivity.class);
        intent.putExtra("selected_category_index", id);
        intent.putExtra("selected_city_id", selectedCityId);
        startActivity(intent);
        this.finish();
    }

    private void doSorting(int id) {
        Intent intent = new Intent(this, SubCategoryActivity.class);
        intent.putExtra("selected_category_index", selectedCategoryIndex);
        intent.putExtra("selected_city_id", selectedCityId);
        intent.putExtra("sorting_index", id);
        startActivity(intent);
        this.finish();
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     * *------------------------------------------------------------------------------------------------
     */

    public void openActivity(int selected_item_id) {
        final Intent intent;
        intent = new Intent(this, DetailActivity.class);
        Utils.psLog("Selected City ID : " + selectedCityId);
        intent.putExtra("selected_item_id", selected_item_id);
        intent.putExtra("selected_city_id", selectedCityId);
        startActivityForResult(intent, 1);
        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Static Class
     * *------------------------------------------------------------------------------------------------
     */
    public class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        Adapter(FragmentManager fm) {
            super(fm);
        }

        void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {

            try {
                return mFragments.size();

            }catch (Exception e) {
                Utils.psErrorLog("Exception",e );
                return 0;
            }
        }

        @Override
        public SpannableString getPageTitle(int position) {
            return Utils.getSpannableString(getApplicationContext(), mFragmentTitles.get(position));
        }

    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Static Class
     **------------------------------------------------------------------------------------------------*/


}
