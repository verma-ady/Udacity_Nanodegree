package com.udacityavijeet.Helper;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class ContentMovie {

    public String ID, Title, URL;
    public Bitmap bitmap;
    public Drawable drawable;

    public ContentMovie(){

    }

    public ContentMovie(String vID, Bitmap vBitmap, String vTitle) {
        ID = vID;
        bitmap = vBitmap;
        Title = vTitle;
    }

    public ContentMovie(Bitmap vBitmap){
        bitmap = vBitmap;
    }

    public void setBitmap(Bitmap Vbitmap ){
        bitmap = Vbitmap;
    }

    public void setDrawable(Drawable vDrawable ){
        drawable = vDrawable;
    }

    public ContentMovie (String vID, String vTitle, String vURL ){
        ID = vID;
        Title = vTitle;
        URL = vURL;
    }
}
