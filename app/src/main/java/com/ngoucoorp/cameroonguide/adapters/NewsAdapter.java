package com.ngoucoorp.cameroonguide.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.ngoucoorp.cameroonguide.GlobalData;
import com.ngoucoorp.cameroonguide.models.PNewsData;
import com.ngoucoorp.cameroonguide.utilities.Utils;
import com.ngoucoorp.cameroonguide.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */
public class NewsAdapter extends BaseAdapter  {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<PNewsData> newsData;
    private Picasso p;

    private ArrayList<PNewsData> filterednewsData;  // for loading  filter data

    public NewsAdapter(Activity activity, ArrayList<PNewsData> newsData, Picasso p) {
        this.activity = activity;
        this.newsData = newsData;
        this.p = p;
    }

    @Override
    public int getCount() {
        if(newsData != null) {
            return newsData.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return newsData.get(position);
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
            if(inflater != null) {
                convertView = inflater.inflate(R.layout.news_row, parent, false);
            }
        }

        if(convertView != null) {

            TextView txtNewsTitle = convertView.findViewById(R.id.news_title);

            TextView txtMessage = convertView.findViewById(R.id.message);

            TextView txtAgo = convertView.findViewById(R.id.ago);


            Context context = parent.getContext();
            if (context != null) {
                txtMessage.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                txtNewsTitle.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                txtAgo.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
            }

            final ImageView imgNewsPhoto = convertView.findViewById(R.id.thumbnail);

            PNewsData news = newsData.get(position);



            txtNewsTitle.setText(news.title);
            String messageStr = news.description.substring(0, Math.min(news.description.length(), 120)) + "...";
            txtMessage.setText(messageStr);
            txtAgo.setText(news.added);
            Utils.psLog(news.images.toString());

            if(news.images.size()>0){
                if (news.images.get(0).path != null) {
//            p.load(Config.APP_IMAGES_URL + news.images.get(0).path)
//                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
//                    .into(imgNewsPhoto);

                    Utils.bindImage(imgNewsPhoto.getContext(), p, imgNewsPhoto, news.images.get(0), 2);
                }
            }


            txtNewsTitle.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
            txtMessage.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
            txtAgo.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
            imgNewsPhoto.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

        }
        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        Log.i("taillef", GlobalData.filterednews.size()+"");
        Log.i("taillen",this.newsData.size()+"");
        newsData.clear();
        if (charText.length() == 0) {
            newsData.addAll( GlobalData.filterednews);
        }
        else
        {
            for (PNewsData news :  GlobalData.filterednews) {
                if (news.title.toLowerCase(Locale.getDefault()).trim().contains(charText)
                        || news.description.toLowerCase(Locale.getDefault()).contains(charText)
                        ) {
                        newsData.add(news);
                }
            }
        }
        notifyDataSetChanged();
    }


}
