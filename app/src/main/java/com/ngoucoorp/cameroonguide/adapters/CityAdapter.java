package com.ngoucoorp.cameroonguide.adapters;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ngoucoorp.cameroonguide.models.PCityData;
import com.ngoucoorp.cameroonguide.utilities.Utils;
import com.ngoucoorp.cameroonguide.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder>  {
    private Activity activity;
    private int lastPosition = -1;
    private List<PCityData> pCityDataList;
    private Picasso p;
    static class CityViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout cv;
        TextView cityName;
        ImageView cityPhoto;
        TextView cityDesc;


        CityViewHolder(View itemView) {
            super(itemView);

            cv = itemView.findViewById(R.id.shop_cv);
            cityName = itemView.findViewById(R.id.city_name);
            cityDesc = itemView.findViewById(R.id.city_desc);
            cityPhoto = itemView.findViewById(R.id.city_photo);


            Context context = cityName.getContext();
            if(context != null) {
                cityName.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                cityDesc.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
            }
        }
    }

    public CityAdapter(Context context, List<PCityData> cities, Picasso p){
        this.activity = (Activity) context;
        this.pCityDataList = cities;
        this.p = p;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_row_container, parent, false);
        return new CityViewHolder(v);
    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public void onBindViewHolder(final CityViewHolder holder, int position) {
        final PCityData city = pCityDataList.get(position);
        holder.cityName.setText(city.name);
        holder.cityName.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

        String cityDescStr = city.description.substring(0, Math.min(city.description.length(), 150)) + "...";
        holder.cityDesc.setText(cityDescStr);
        holder.cityDesc.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));


//        p.load(Config.APP_IMAGES_URL + city.cover_image_file)
//                .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
//                .placeholder(R.drawable.ps_icon)
//                .into(holder.cityPhoto);

        Utils.bindImage(holder.cityPhoto.getContext(), p,  holder.cityPhoto, city.cover_image_file, 1 );

        setAnimation(holder.cv, position);



        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                final Intent intent;
//                intent = new Intent(holder.itemView.getContext(),SelectedCityActivity.class);
//                GlobalData.citydata = city;
//                intent.putExtra("selected_city_id", city.id);
//                holder.itemView.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        if(pCityDataList != null) {
            return pCityDataList.size();
        }
        return 0;
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        if (position > lastPosition)
        {
            @SuppressLint("PrivateResource") Animation animation = AnimationUtils.loadAnimation(activity, R.anim.abc_slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }else{
            lastPosition = position;
        }
    }

}
