package com.ngoucoorp.cameroonguide.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */
public class PNewsData implements Parcelable {
    public int id;
    public int city_id;
    public String title;
    public String description;
    public int is_published;
    public String added;
    public String phone;
    public String date_event;
    public String time_event;
    public String time_format;
    public String website_event;
    public ArrayList<PImageData> images;

    public PNewsData(Parcel in) {
        id = in.readInt();
        city_id = in.readInt();
        title = in.readString();
        description = in.readString();
        is_published = in.readInt();
        added = in.readString();

        phone = in.readString();
        date_event = in.readString();
        time_event = in.readString();
        time_format = in.readString();
        website_event = in.readString();

        if (in.readByte() == 0x01) {
            images = new ArrayList<>();
            in.readList(images, PImageData.class.getClassLoader());
        } else {
            images = null;
        }
    }

    public PNewsData(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(city_id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(is_published);
        dest.writeString(added);
        dest.writeString(phone);
        dest.writeString(date_event);
        dest.writeString(time_event);
        dest.writeString(time_format);
        dest.writeString(website_event);
        if (images == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(images);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PNewsData> CREATOR = new Parcelable.Creator<PNewsData>() {
        @Override
        public PNewsData createFromParcel(Parcel in) {
            return new PNewsData(in);
        }

        @Override
        public PNewsData[] newArray(int size) {
            return new PNewsData[size];
        }
    };
}