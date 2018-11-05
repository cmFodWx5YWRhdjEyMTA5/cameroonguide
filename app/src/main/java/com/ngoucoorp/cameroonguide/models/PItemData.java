package com.ngoucoorp.cameroonguide.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */
public class PItemData implements Parcelable {

    public int id;

    public int cat_id;

    public int sub_cat_id;

    public int city_id;

    public String name;

    public String description;

    public String address;

    public String phone;

    public String email;

    public String lat;

    public String lng;

    public String search_tag;

    public int is_published;

    public String added;

    public String updated;

    public String like_count;

    public String review_count;
    public String website;

    public String inquiries_count;

    public ArrayList<PReviewData> reviews;

    public ArrayList<PImageData> images;

    protected PItemData(Parcel in) {
        id = in.readInt();
        cat_id = in.readInt();
        sub_cat_id = in.readInt();
        city_id = in.readInt();
        name = in.readString();
        description = in.readString();
        address = in.readString();
        phone = in.readString();
        email = in.readString();
        website = in.readString();
        lat = in.readString();
        lng = in.readString();
        search_tag = in.readString();
        is_published = in.readInt();
        added = in.readString();
        updated = in.readString();
        like_count = in.readString();
        review_count = in.readString();
        inquiries_count = in.readString();
        if (in.readByte() == 0x01) {
            reviews = new ArrayList<PReviewData>();
            in.readList(reviews, PReviewData.class.getClassLoader());
        } else {
            reviews = null;
        }
        if (in.readByte() == 0x01) {
            images = new ArrayList<PImageData>();
            in.readList(images, PImageData.class.getClassLoader());
        } else {
            images = null;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(cat_id);
        dest.writeInt(sub_cat_id);
        dest.writeInt(city_id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(website);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeString(search_tag);
        dest.writeInt(is_published);
        dest.writeString(added);
        dest.writeString(updated);
        dest.writeString(like_count);
        dest.writeString(review_count);
        dest.writeString(inquiries_count);
        if (reviews == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(reviews);
        }
        if (images == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(images);
        }

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PItemData> CREATOR = new Parcelable.Creator<PItemData>() {
        @Override
        public PItemData createFromParcel(Parcel in) {
            return new PItemData(in);
        }

        @Override
        public PItemData[] newArray(int size) {
            return new PItemData[size];
        }
    };
}