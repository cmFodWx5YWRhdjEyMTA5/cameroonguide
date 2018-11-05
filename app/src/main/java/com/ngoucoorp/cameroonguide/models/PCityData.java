package com.ngoucoorp.cameroonguide.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */
public class PCityData implements Parcelable {

    public int id;

    public String name;

    public String description;

    public String address;

    public String lat;

    public String lng;

    public String added;

    public int status;

    public int item_count;

    public int category_count;

    public int sub_category_count;

    public int follow_count;

    public String cover_image_file;

    public int cover_image_width;

    public int cover_image_height;

    public String cover_image_description;

    public ArrayList<PCategoryData> categories;

    protected PCityData(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        address = in.readString();
        lat = in.readString();
        lng = in.readString();
        added = in.readString();
        status = in.readInt();
        item_count = in.readInt();
        category_count = in.readInt();
        sub_category_count = in.readInt();
        follow_count = in.readInt();
        cover_image_file = in.readString();
        cover_image_width = in.readInt();
        cover_image_height = in.readInt();
        cover_image_description = in.readString();
        if (in.readByte() == 0x01) {
            categories = new ArrayList<PCategoryData>();
            in.readList(categories, PCategoryData.class.getClassLoader());
        } else {
            categories = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(address);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeString(added);
        dest.writeInt(status);
        dest.writeInt(item_count);
        dest.writeInt(category_count);
        dest.writeInt(sub_category_count);
        dest.writeInt(follow_count);
        dest.writeString(cover_image_file);
        dest.writeInt(cover_image_width);
        dest.writeInt(cover_image_height);
        dest.writeString(cover_image_description);
        if (categories == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(categories);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PCityData> CREATOR = new Parcelable.Creator<PCityData>() {
        @Override
        public PCityData createFromParcel(Parcel in) {
            return new PCityData(in);
        }

        @Override
        public PCityData[] newArray(int size) {
            return new PCityData[size];
        }
    };
}
