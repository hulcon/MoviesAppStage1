package com.example.irshad.moviesappstage1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.irshad.moviesappstage1.Models.Movie;
import com.example.irshad.moviesappstage1.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String  TAG = "MainActivity";
    private ArrayList<Movie> movieArrayList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private MovieAdapter movieAdapter;
    private ProgressBar mProgressBar;
    private TextView textViewLoadingMessage;
    private ImageView imageViewNoInternet;
    private String currentSortOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.main_screen_recyclerview);
        mProgressBar = findViewById(R.id.main_screen_progressBar);
        textViewLoadingMessage = findViewById(R.id.textView_loading_message);

        /* Setup RecyclerView Adapter */
        movieAdapter = new MovieAdapter(this,movieArrayList);
        mRecyclerView.setAdapter(movieAdapter);

        /* Setup Grid Layout Manager for RecyclerView */
        final int columns = getResources().getInteger(R.integer.movie_grid_columns);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,columns));

        String titleString;
        if(Constants.DEFAULT_SORT_ORDER.equals(Constants.SORT_ORDER_POPULAR)){
            titleString = getResources().getString(R.string.sort_movies_by_popularity);
        } else {
            titleString = getResources().getString(R.string.sort_movies_by_rating);
        }
        currentSortOrder = Constants.DEFAULT_SORT_ORDER;
        //getSupportActionBar().setTitle(titleString);

        getMovieDataFromServerIntoRecyclerView(Constants.DEFAULT_SORT_ORDER);
    }

    /**
     * This method checks whether the device is online or not. If the device is
     * connected to the internet, it fires the {@link MainActivity#sendNetworkRequest(String)}
     * which initiates the actual HTTPS API request. This method also displays an
     * indeterminate progress bar. If the device is offline, it shows an error.
     * @param sortOrder Movie sort order, either "popular" or "toprated" defined by
     *                  the constants {@link Constants#SORT_ORDER_POPULAR} and
     *                  {@link Constants#SORT_ORDER_TOP_RATED}
     */
    public void getMovieDataFromServerIntoRecyclerView(String sortOrder){
        /* Check Internet connectivity first. Proceed with network request only if the
         * device is connected to the Internet. Otherwise, display an error message
         */
        if(NetworkUtils.isOnline(this)){
            /* Device is connected to the Internet, proceed with the network request*/
            sendNetworkRequest(sortOrder);

            /* Hide RecyclerView, no Internet Image and Display the Progress Bar and Loading Message */
            imageViewNoInternet = findViewById(R.id.imageView_no_internet);
            imageViewNoInternet.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            textViewLoadingMessage.setVisibility(View.VISIBLE);

        } else {
            /* Device is not connected to the Internet, display error message */
            imageViewNoInternet = findViewById(R.id.imageView_no_internet);
            imageViewNoInternet.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Not connected to Internet. Please connect to Internet first!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method parses the string response received from the Movie DB Server
     * and extracts the required values and stores them in an ArrayList of
     * type{@link MovieAdapter}
     * @param serverResponse JSON data in the form of string response
     *                       received from the server
     */
    public void getMovieDetailsFromServerResponse(String serverResponse){
        try {
            JSONObject response = new JSONObject(serverResponse);
            JSONArray resultsJsonArray = response.getJSONArray("results");
            for(int i=0;i<resultsJsonArray.length();i++){
                JSONObject result = resultsJsonArray.getJSONObject(i);
                Movie movie = new Movie();
                movie.setTitle(result.getString("title"));
                movie.setRating(result.getDouble("vote_average"));
                movie.setThumbnailPath(result.getString("poster_path"));
                movie.setBackdropPath(result.getString("backdrop_path"));
                movie.setPopularity(result.getLong("popularity"));
                movie.setMovieOverview(result.getString("overview"));
                movie.setOriginalTitle(result.getString("original_title"));
                movie.setId(result.getString("id"));
                movie.setReleaseDate(result.getString("release_date"));
                movieArrayList.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method sets up a Volley request and sends an HTTPS API Call
     * to TMD server. This method does not parse the response received
     * from the server. On receiving a response, this method calls
     * {@link MainActivity#getMovieDetailsFromServerResponse(String)} to
     * parse the response.
     */
    public void sendNetworkRequest(String sortOrder){
        /* Create the TMD API URL */
        Uri builtUri = Uri.parse(Constants.BASE_URL_MOVIES)
                .buildUpon()
                .appendPath(sortOrder)
                .appendQueryParameter(Constants.QUERY_PARAM_API_KEY, Constants.API_KEY)
                .build();
        String url = builtUri.toString();
        Log.d(TAG,"Url is " + url);

        /* Instantiate Volley RequestQueue */
        RequestQueue queue = Volley.newRequestQueue(this);
        /* Request a string response from the provided URL */
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /* On receiving response, process the JSON response
                         * into ArrayList and hide the
                         * progress bar and loading message
                         */
                        getMovieDetailsFromServerResponse(response);
                        movieAdapter.notifyDataSetChanged();
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        textViewLoadingMessage.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mRecyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                textViewLoadingMessage.setVisibility(View.VISIBLE);
                textViewLoadingMessage.setText(R.string.server_error);
            }
        });
        /* Add the request to the RequestQueue */
        queue.add(stringRequest);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_activity_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMenuItem = item.getItemId();
        switch (selectedMenuItem){
            case R.id.action_sort_movies_by_popularity:
                if(!currentSortOrder.equals(Constants.SORT_ORDER_POPULAR)){
                    /* Sort movies by popularity */
                    currentSortOrder = Constants.SORT_ORDER_POPULAR;
                    getSupportActionBar().setTitle(R.string.sort_movies_by_popularity);
                    /* Clear previous data from the adapter */
                    movieAdapter.clearAllData();
                    /* Display new movie list in the recyclerview */
                    getMovieDataFromServerIntoRecyclerView(currentSortOrder);
                }
                break;

            case R.id.action_sort_movies_by_rating:
                if(!currentSortOrder.equals(Constants.SORT_ORDER_TOP_RATED)){
                    /* Sort movies by rating */
                    currentSortOrder = Constants.SORT_ORDER_TOP_RATED;
                    getSupportActionBar().setTitle(R.string.sort_movies_by_rating);
                    /* Clear previous data from the adapter */
                    movieAdapter.clearAllData();
                    /* Display new movie list in the recyclerview */
                    getMovieDataFromServerIntoRecyclerView(currentSortOrder);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}



