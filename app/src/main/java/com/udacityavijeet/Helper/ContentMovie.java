package com.udacityavijeet.Helper;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ContentMovie implements Parcelable{

    public String details[] ;//ID, Title, URL;
    public ArrayList<String> trailerKey, trailerName, reviewBy, reviewText;
    public Bitmap bitmap;
    public Drawable drawable;

    public ContentMovie(){
        trailerKey = new ArrayList<>();
        trailerName = new ArrayList<>();
        reviewBy = new ArrayList<>();
        reviewText = new ArrayList<>();
        details = new String[7];
        bitmap = null;
    }

    public ContentMovie(Parcel in ){
        //add them and extract them from parcel
        trailerKey = new ArrayList<>();
        trailerName = new ArrayList<>();
        reviewBy = new ArrayList<>();
        reviewText = new ArrayList<>();

        bitmap = null;
        in.readStringArray(details);
    }

    public ContentMovie (String vID, String vTitle, String vURL,  String vSynopsys, String vRating, String vRelease, String vBackDrop  ){
        trailerKey = new ArrayList<>();
        trailerName = new ArrayList<>();
        details = new String[7];
        details[0] = vID;
        details[1] = vTitle;
        details[2] = vURL;
        details[3] = vSynopsys;
        details[4] = vRating;
        details[5] = vRelease;
        details[6] = vBackDrop;
        bitmap = null;
    }

    public void setBitmap(Bitmap Vbitmap ){
        bitmap = Vbitmap;
    }

    public void setDrawable(Drawable vDrawable ){
        drawable = vDrawable;
    }

    public static final Parcelable.Creator<ContentMovie> CREATOR = new Parcelable.Creator<ContentMovie>() {
        public ContentMovie createFromParcel(Parcel in) {
            return new ContentMovie(in);
        }

        public ContentMovie[] newArray(int size) {
            return new ContentMovie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(details);
    }
}
