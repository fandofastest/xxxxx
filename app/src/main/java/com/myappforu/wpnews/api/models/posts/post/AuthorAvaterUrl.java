package com.myappforu.wpnews.api.models.posts.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SAIF-MCC on 10/8/2017.
 */

public class AuthorAvaterUrl implements Parcelable {
    @SerializedName("96")
    private String mSourceUrl;

    public AuthorAvaterUrl() {
    }

    public AuthorAvaterUrl(String sourceUrl) {
        mSourceUrl = sourceUrl;
    }

    public String getSourceUrl() {
        return mSourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        mSourceUrl = sourceUrl;
    }

    public static Creator<AuthorAvaterUrl> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mSourceUrl);
    }

    protected AuthorAvaterUrl(Parcel in) {
        mSourceUrl = in.readString();
    }

    public static final Creator<AuthorAvaterUrl> CREATOR = new Creator<AuthorAvaterUrl>() {
        @Override
        public AuthorAvaterUrl createFromParcel(Parcel source) {
            return new AuthorAvaterUrl(source);
        }

        @Override
        public AuthorAvaterUrl[] newArray(int size) {
            return new AuthorAvaterUrl[size];
        }
    };


}