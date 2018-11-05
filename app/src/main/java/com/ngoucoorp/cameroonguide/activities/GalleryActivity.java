package com.ngoucoorp.cameroonguide.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ngoucoorp.cameroonguide.models.PAboutData;
import com.ngoucoorp.cameroonguide.models.PImageData;
import com.ngoucoorp.cameroonguide.models.PItemData;
import com.ngoucoorp.cameroonguide.models.PNewsData;
import com.ngoucoorp.cameroonguide.uis.ExtendedViewPager;
import com.ngoucoorp.cameroonguide.uis.TouchImageView;
import com.ngoucoorp.cameroonguide.R;
import com.ngoucoorp.cameroonguide.utilities.Utils;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */

public class GalleryActivity extends AppCompatActivity {

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     * *------------------------------------------------------------------------------------------------
     */

    private TextView txtImgDesc;
    private static ArrayList<PImageData> imageArray;

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
        setContentView(R.layout.activity_gallery);

        initData();

        initUI();


    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    class TouchImageAdapter extends PagerAdapter {

        Picasso p;

        TouchImageAdapter(Picasso p) {
            this.p = p;
        }

        @Override
        public int getCount() {
            if (imageArray != null) {
                return imageArray.size();
            }

            return 0;
        }


        @NonNull
        @Override
        public View instantiateItem(@NonNull ViewGroup container, int position) {
            TouchImageView imgView = new TouchImageView(container.getContext());

            if (imageArray != null) {
                if (position >= imageArray.size()) {
                    position = position % imageArray.size();
                }

                //Version 1
//                p.load(Config.APP_IMAGES_URL + imageArray.get(position).path).transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT)).placeholder(R.drawable.ps_icon)
//                        .into(imgView);
                Utils.bindImage(getApplicationContext(), p, imgView, imageArray.get(position).path, 1);


                container.addView(imgView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            }

            return imgView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
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
        try {

            Picasso p = new Picasso.Builder(this)
                    .memoryCache(new LruCache(1))
                    .build();

            ExtendedViewPager mViewPager = findViewById(R.id.view_pager);
            mViewPager.setAdapter(new TouchImageAdapter(p));
            //mViewPager.setCurrentItem((Integer.MAX_VALUE / 2) - (Integer.MAX_VALUE / 2) % 12);
            txtImgDesc = findViewById(R.id.img_desc);

            if (imageArray != null && imageArray.size() > 0) {
                txtImgDesc.setText(imageArray.get(0).description);
            } else {
                txtImgDesc.setText("");
            }
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                public void onPageScrollStateChanged(int arg0) {

                }

                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                public void onPageSelected(int currentPage) {

                    if (imageArray != null) {
                        if (currentPage >= imageArray.size()) {
                            currentPage = currentPage % imageArray.size();
                        }

                        //currentPage is the position that is currently displayed.
                        txtImgDesc.setText(imageArray.get(currentPage).description);
                    }
                }

            });
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initUI.", e);
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
            Bundle bundle = getIntent().getBundleExtra("images_bundle");
            String fromStr = bundle.getString("from");
            if (fromStr != null)
                switch (fromStr) {
                    case "item":
                        try {
                            PItemData itemData = bundle.getParcelable("images");
                            if (itemData != null) {
                                imageArray = itemData.images;
                            }
                        } catch (Exception e) {
                            Utils.psErrorLog("initData", e);
                        }
                        break;
                    case "about":
                        try {
                            PAboutData aboutData = bundle.getParcelable("images");
                            if (aboutData != null) {
                                imageArray = aboutData.images;
                            }
                        } catch (Exception e) {
                            Utils.psErrorLog("initData", e);
                        }
                        break;
                    default:
                        try {
                            PNewsData newsData = bundle.getParcelable("images");
                            if (newsData != null) {
                                imageArray = newsData.images;
                            }
                        } catch (Exception e) {
                            Utils.psErrorLog("initData", e);
                        }
                        break;
                }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initData.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

}
