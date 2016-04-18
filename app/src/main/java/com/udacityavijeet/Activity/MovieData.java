package com.udacityavijeet.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.udacityavijeet.Helper.AppController;
import com.udacityavijeet.Helper.Keys;
import com.udacityavijeet.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieData extends AppCompatActivity {

    ImageView poster, backdrop;
    TextView title, overview, userR, releaseD;
    final private String  tag_string_req = "string_req";
    final private String BASE_URL = "http://api.themoviedb.org/3/movie";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_data);
        String name = getIntent().getStringExtra("movieTitle");
        String synopsis = getIntent().getStringExtra("movieSynopsis");
        String rating = getIntent().getStringExtra("movieUR");
        String backdropURL = getIntent().getStringExtra("movieBackdrop");
        String posterURL = getIntent().getStringExtra("moviePoster");
        String release = getIntent().getStringExtra("movieRD");
        String movieID = getIntent().getStringExtra("movieID");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(name);

        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = (point.x)/2;
        int layoutW, layoutH;
        layoutW = width-15;
        layoutH = (int)(1.5 * (width-15));

//        poster = (ImageView) findViewById(R.id.imageMV_Poster);
        backdrop = (ImageView) findViewById(R.id.imageMV_backdrop);
//        downloadIMG(posterURL, poster, layoutW, layoutH);
        downloadIMG(backdropURL, backdrop, layoutW, layoutH);

//        title = (TextView) findViewById(R.id.textMV_Name);
        overview = (TextView) findViewById(R.id.textMV_Synopsis);
        userR = (TextView) findViewById(R.id.textMV_UserR);
        releaseD = (TextView) findViewById(R.id.textMV_RD);

//        title.setText(name);
        overview.setText(synopsis);
        userR.setText("Rating : " + rating);
        releaseD.setText("Release Date : " + release);
        Uri uri = Uri.parse(BASE_URL).buildUpon().appendPath(movieID)
                .appendQueryParameter("api_key", Keys.TMDB_KEY)
                .appendQueryParameter("append_to_response", "videos" ).build();
        getMovieData(uri.toString());
    }

    private void fabListener(FloatingActionButton fab, final String key){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Trailer to be added in Stage 2", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key )));
            }
        });
    }

    private void getMovieData (final String url ){
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("MyApp", response.toString());
                try {
                    JSONObject movieData = new JSONObject(response.toString());
                    JSONArray result = movieData.getJSONObject("videos").getJSONArray("results");
                    String YTkey = result.getJSONObject(0).getString("key");
                    fabListener((FloatingActionButton) findViewById(R.id.fab_play), YTkey);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("MyApp", "Error: " + error.getMessage());
            }
        });

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void downloadIMG (final String url, final ImageView imageViewIMG, final int layoutW, final int layoutH ){
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

// If you are using normal ImageView
        imageLoader.get(url, new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MyApp", "Image Load Error: " + error.getMessage());
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    Bitmap temp = Bitmap.createScaledBitmap(response.getBitmap(), layoutW, layoutH, true);
                    imageViewIMG.setImageBitmap(temp);
                }
            }
        });
    }
}
