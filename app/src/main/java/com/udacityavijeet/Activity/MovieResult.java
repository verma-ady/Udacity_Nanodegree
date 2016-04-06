package com.udacityavijeet.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.udacityavijeet.R;

public class MovieResult extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_result);
        Log.v("MyApp", getClass().toString() + getIntent().getExtras().getString("movie"));
    }
}
