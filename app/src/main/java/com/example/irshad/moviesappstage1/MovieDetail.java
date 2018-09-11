package com.example.irshad.moviesappstage1;

import android.net.Uri;
import android.os.Bundle;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.irshad.moviesappstage1.Models.Movie;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MovieDetail extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "id";
    private final String  TAG = "MovieDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.movie_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" ");
        String movieId = getIntent().getStringExtra(EXTRA_MOVIE_ID);
        showLoading(true);
        sendNetworkRequest(movieId);

    }



    /**
     * This method sets up a Volley request and sends an HTTPS API Call
     * to TMD server. This method parses the JSON data and in turn calls
     * the {@link MovieDetail#displayMovieDetails(Movie)} method to
     * display movie details on screen.
     */
    public void sendNetworkRequest(String id){
        /* Create the TMD API URL */
        Uri builtUri = Uri.parse(Constants.BASE_URL_MOVIES)
                .buildUpon()
                .appendPath(id)
                .appendQueryParameter(Constants.QUERY_PARAM_API_KEY, Constants.API_KEY)
                .appendQueryParameter("language","en-US")
                .build();
        String url = builtUri.toString();
        //Log.d(TAG,"Url is " + url);

        /* Instantiate Volley RequestQueue */
        RequestQueue queue = Volley.newRequestQueue(this);
        /* Request a string response from the provided URL */
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /* On receiving response, process the JSON response
                         * and hide the
                         * progress bar and loading message
                         */
                        try {
                            JSONObject result = new JSONObject(response);
                            Movie movie = new Movie();
                            movie.setTitle(result.getString("title"));
                            movie.setRating(result.getDouble("vote_average"));
                            movie.setThumbnailPath(result.getString("poster_path"));
                            movie.setBackdropPath(result.getString("backdrop_path"));
                            movie.setPopularity(result.getLong("popularity"));
                            movie.setMovieOverview(result.getString("overview"));
                            movie.setOriginalTitle(result.getString("original_title"));
                            movie.setReleaseDate(result.getString("release_date"));
                            movie.setId(result.getString("id"));
                            displayMovieDetails(movie);
                            showLoading(false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"Error in getting movie details!!");
                showError("Error in getting movie details!!!");
            }
        });
        /* Add the request to the RequestQueue */
        queue.add(stringRequest);
    }

    /**
     * This method displays the various details of a movie.
     * @param movie Object of type {@link Movie} containing details about the movie.
     */
    private void displayMovieDetails(Movie movie){
        ImageView movieBackdrop = findViewById(R.id.movie_backdrop);
        String backdropPath = Constants.BASE_URL_POSTER_PATH + Constants.POSTER_RESOULTION + movie.getBackdropPath();
        Log.d(TAG,"Backdrop Path is: " + backdropPath);
        Picasso.get().load(backdropPath).into(movieBackdrop);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.movie_detail_collapsing_toolbar);
        collapsingToolbarLayout.setTitle(movie.getTitle());

        String moviePosterPath = Constants.BASE_URL_POSTER_PATH + Constants.POSTER_RESOULTION + movie.getThumbnailPath();
        ImageView moviePoster = findViewById(R.id.imageview_movie_poster);
        Picasso.get().load(moviePosterPath).into(moviePoster);

        TextView movieTitle = findViewById(R.id.textview_movie_title);
        movieTitle.setText(movie.getTitle());

        TextView releaseDate = findViewById(R.id.textview_release_date);
        releaseDate.setText(movie.getReleaseDate());

        TextView plotSynopsis = findViewById(R.id.textview_plot_synopsis);
        plotSynopsis.setText(movie.getMovieOverview());

        RatingBar ratingBar = findViewById(R.id.ratingBar_movie_rating);
        ratingBar.setRating((float)movie.getRating());

        TextView ratingText = findViewById(R.id.textview_rating_text);
        String stringRatingText = movie.getRating() + "/" + "10";
        ratingText.setText(stringRatingText);
    }

    /*
     * This code provides "Up" navigation for the back button in appbar
     * of this activity. I simply call the finish method to end this
     * activity.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLoading(boolean show){
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout_movie_details);
        TextView loadingMessage = findViewById(R.id.textView_loading_message);
        ProgressBar progressBar = findViewById(R.id.movie_detail_progressBar);

        if(show){
            constraintLayout.setVisibility(View.INVISIBLE);
            loadingMessage.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            constraintLayout.setVisibility(View.VISIBLE);
            loadingMessage.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showError(String errorMessage){
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout_movie_details);
        TextView loadingMessage = findViewById(R.id.textView_loading_message);
        ProgressBar progressBar = findViewById(R.id.movie_detail_progressBar);

        constraintLayout.setVisibility(View.INVISIBLE);
        loadingMessage.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        loadingMessage.setText(errorMessage);
    }

}
