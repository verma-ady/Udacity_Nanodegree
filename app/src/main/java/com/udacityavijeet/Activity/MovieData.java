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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.udacityavijeet.Helper.ContentMovie;
import com.udacityavijeet.Helper.Keys;
import com.udacityavijeet.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieData extends AppCompatActivity {

    ImageView poster, backdrop;
    TextView title, overview, userR, releaseD;
    final private String  tag_string_req = "string_req";
    final private String BASE_URL = "http://api.themoviedb.org/3/movie";
    ContentMovie contentMovie;
    RVAdapterTrailers rvAdapterTrailers;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_data);
        contentMovie = new ContentMovie();
        contentMovie.details[1] = getIntent().getStringExtra("movieTitle");
        contentMovie.details[3] = getIntent().getStringExtra("movieSynopsis");
        contentMovie.details[4] = getIntent().getStringExtra("movieUR");
        contentMovie.details[6] = getIntent().getStringExtra("movieBackdrop");
        contentMovie.details[2] = getIntent().getStringExtra("moviePoster");
        contentMovie.details[5] = getIntent().getStringExtra("movieRD");
        contentMovie.details[0] = getIntent().getStringExtra("movieID");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(contentMovie.details[1]);

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
        downloadIMG(contentMovie.details[6], backdrop, layoutW, layoutH);
//
////        title = (TextView) findViewById(R.id.textMV_Name);
//        overview = (TextView) findViewById(R.id.textMV_Synopsis);
//        userR = (TextView) findViewById(R.id.textMV_UserR);
//        releaseD = (TextView) findViewById(R.id.textMV_RD);
//
////        title.setText(name);
//        overview.setText(contentMovie.details[3]);
//        userR.setText("Rating : " + contentMovie.details[4]);
//        releaseD.setText("Release Date : " + contentMovie.details[5]);

        recyclerView = (RecyclerView) findViewById(R.id.recycleTrailer);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        Uri uri = Uri.parse(BASE_URL).buildUpon().appendPath(contentMovie.details[0])
                .appendQueryParameter("api_key", Keys.TMDB_KEY)
                .appendQueryParameter("append_to_response", "videos" ).build();
        getMovieData(uri.toString());

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
                    int len = result.length();
                    for (int i=0; i<len ; i++ ){
                        contentMovie.trailerKey.add(result.getJSONObject(i).getString("key"));
                        contentMovie.trailerName.add(result.getJSONObject(i).getString("name"));
                        Log.d("MyApp", "Key:" + contentMovie.trailerKey.get(i) + " Name:" + contentMovie.trailerName.get(i));
                    }
                    rvAdapterTrailers = new RVAdapterTrailers(contentMovie.trailerName);
                    recyclerView.setAdapter(rvAdapterTrailers);
//                    fabListener((FloatingActionButton) findViewById(R.id.fab_play), YTkey);
                } catch (JSONException e) {
                    Log.e("MyApp","getMovieData VolleyError" + e.toString() );
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

    public class RVAdapterTrailers extends RecyclerView.Adapter<RVAdapterTrailers.CardViewHolder> {
        private ArrayList<String> Name;
        public RVAdapterTrailers ( ArrayList<String> vName){
            Log.v("MyApp", "RVAdapterTrailers");
            Name = vName;
        }

        @Override
        public RVAdapterTrailers.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_trailer, parent, false );
            CardViewHolder cardViewHolder = new CardViewHolder( view );
            return cardViewHolder;
        }

        @Override
        public void onBindViewHolder(RVAdapterTrailers.CardViewHolder holder, int position) {
            Log.v("MyApp", "RVAdapterTrailers onBindViewHolder" + position );
            holder.text.setText(Name.get(position));
            holder.image.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play_circle_filled_black_24dp));
        }

        @Override
        public int getItemCount() {
            return Name.size();
        }

        public class CardViewHolder extends RecyclerView.ViewHolder {
            CardView cardView;
            TextView text;
            ImageView image;
            public CardViewHolder(View itemView) {
                super(itemView);
                cardView = (CardView) itemView.findViewById(R.id.cardTrailer);
                text = (TextView) itemView.findViewById(R.id.textTrailer);
                image = (ImageView) itemView.findViewById(R.id.imageTrailer);
            }
        }
    }

}
