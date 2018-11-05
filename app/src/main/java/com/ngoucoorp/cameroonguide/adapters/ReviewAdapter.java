package com.ngoucoorp.cameroonguide.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngoucoorp.cameroonguide.models.PReviewData;
import com.ngoucoorp.cameroonguide.utilities.Utils;
import com.ngoucoorp.cameroonguide.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */
public class ReviewAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<PReviewData> reviewData;
    private Picasso p;

    public ReviewAdapter(Activity activity, ArrayList<PReviewData> reviewData, Picasso p) {
        this.activity = activity;
        this.reviewData = reviewData;
        this.p = p;
    }

    @Override
    public int getCount() {
        if (reviewData != null) {
            return reviewData.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return reviewData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null) {
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            if (inflater != null) {
                convertView = inflater.inflate(R.layout.review_row, parent, false);
            }
        }


        if (convertView != null) {
            TextView txtUserName = convertView.findViewById(R.id.user_name);

            TextView txtMessage = convertView.findViewById(R.id.message);

            TextView txtAgo = convertView.findViewById(R.id.ago);

            Context context = convertView.getContext();

            if (context != null) {
                txtMessage.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                txtUserName.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                txtAgo.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
            }
            final ImageView imgUserPhoto = convertView.findViewById(R.id.thumbnail);

            PReviewData review = reviewData.get(position);

            txtUserName.setText(review.appuser_name);
            txtMessage.setText(review.review);
            txtAgo.setText(review.added);

//        p.load(Config.APP_IMAGES_URL + review.profile_photo)
//                .placeholder(R.drawable.ic_person_black)
//                .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
//                .into(imgUserPhoto);

            Utils.bindImage(imgUserPhoto.getContext(), p, imgUserPhoto, review.profile_photo, 2);

            txtUserName.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
            txtMessage.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
            txtAgo.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
            imgUserPhoto.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

        }
        return convertView;
    }
}
