package com.udacityavijeet.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import com.squareup.picasso.Target;
import com.udacityavijeet.Helper.ContentMovie;
import com.udacityavijeet.Helper.ImageAdapter;
import com.udacityavijeet.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Home extends AppCompatActivity {

    GridView gridView;
    ImageAdapter imageAdapter;
    ContentMovie contentMovie[];
    final String BaseURL = "http://api.themoviedb.org/3/discover/movie";
    final String BaseIMG = "http://image.tmdb.org/t/p/original";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        gridView = (GridView) findViewById(R.id.gridView_category);

        SearchAPI searchAPI = new SearchAPI();
        searchAPI.execute();
    }

    private void gridListener(){
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("MyApp", contentMovie[position].ID + " " + contentMovie[position].Title );

            }
        });
    }

    public class SearchAPI extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            String error = null;

            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            URL url = null;
            try {

                Uri uri = Uri.parse(BaseURL).buildUpon().appendQueryParameter("sort_by", "popularity.desc")
                        .appendQueryParameter("api_key", getString(R.string.api)).build();

                Log.v("MyApp", getClass().toString() + " " + uri.toString());
                url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return "null_inputstream";
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line + '\n');
                }

                if (buffer.length() == 0) {
                    return "null_inputstream";
                }

                String stringJSON = buffer.toString();
                return stringJSON;
            } catch (UnknownHostException | ConnectException e) {
                error = "null_internet";
                e.printStackTrace();
            } catch (IOException e) {
                error = "null_file";
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.e("MyApp", "ErrorClosingStream", e);
                    }
                }
            }
            return error;
        }//doinbackground

        @Override
        protected void onPostExecute(String strJSON) {

            if (strJSON.equals("null_inputstream") || strJSON.equals("null_file")) {
                Toast.makeText(Home.this, "Unable to Connect to Internet", Toast.LENGTH_SHORT).show();
                return;
            }

            if (strJSON.equals("null_internet")) {
                Toast.makeText(Home.this, "No Internet Connectivity", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.v("MyApp", getClass().toString() + strJSON);
            try {
                JSONObject jsonObject = new JSONObject(strJSON);
                JSONArray results = jsonObject.getJSONArray("results");

                contentMovie = new ContentMovie[results.length()];

                for (int i = 0; i < results.length(); i++) {
                    JSONObject movie = results.getJSONObject(i);
                    contentMovie[i] = new ContentMovie(movie.getString("id"), movie.getString("title"), BaseIMG + movie.getString("poster_path"));
                }

                imageAdapter = new ImageAdapter(getApplicationContext(), Arrays.asList(contentMovie));
                gridView.setAdapter(imageAdapter);
//                GetBitmap getBitmap = new GetBitmap();
//                getBitmap.execute();
                Log.v("MyApp", getClass().toString() + " End of onPost ");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}