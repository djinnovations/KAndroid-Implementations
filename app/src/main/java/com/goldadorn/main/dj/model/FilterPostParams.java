package com.goldadorn.main.dj.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DJphy on 18-06-2016.
 */
public class FilterPostParams implements Parcelable {

    private String userid;
    private String postid;
    private String offset;

    public FilterPostParams(String userid, String postid, String offset) {
        this.userid = userid;
        this.postid = postid;
        this.offset = offset;
    }


    public String getUserid() {
        return userid;
    }

    public String getPostid() {
        return postid;
    }

    public String getOffset() {
        return offset;
    }

    protected FilterPostParams(Parcel in) {
        userid = in.readString();
        postid = in.readString();
        offset = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userid);
        dest.writeString(postid);
        dest.writeString(offset);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FilterPostParams> CREATOR = new Parcelable.Creator<FilterPostParams>() {
        @Override
        public FilterPostParams createFromParcel(Parcel in) {
            return new FilterPostParams(in);
        }

        @Override
        public FilterPostParams[] newArray(int size) {
            return new FilterPostParams[size];
        }
    };
}
