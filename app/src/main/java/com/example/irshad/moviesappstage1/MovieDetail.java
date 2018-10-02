package com.example.irshad.moviesappstage1;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.irshad.moviesappstage1.Database.AppDatabase;
import com.example.irshad.moviesappstage1.Models.Movie;
import com.example.irshad.moviesappstage1.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MovieDetail extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "id";
    private final String  TAG = "MovieDetail";
    private FloatingActionButton floatingActionButton;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.movie_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" ");
        /* Get Movie details from the parcelable extra "movie" object */
        final Movie movie = getIntent().getParcelableExtra(MovieAdapter.EXTRA_MOVIE);
        displayMovieDetails(movie);

        mDb = AppDatabase.getsInstance(getApplicationContext());

        /* If the device is online, try to fetch the trailers*/
        if(NetworkUtils.isOnline(this)){
            getTrailerInfoFromServer(movie.getId());
            getReviewsInfoFromServer(movie.getId());
        } else{
            //Display a message showing the device is offline
            Toast.makeText(this, "Trailers and Reviews not available as your device is offline", Toast.LENGTH_SHORT).show();
        }

        floatingActionButton = findViewById(R.id.fab_movie_detail);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Write code here to add movie to the favourites
                saveMovieAsFavourite(movie);
            }
        });
    }

    private void saveMovieAsFavourite(final Movie movie) {
        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                /* Check if the movie already exists in the favourites database.
                 * If it already exists then remove it from the favourites,
                 * otherwise insert it into the database.
                 */
                int movieCount = mDb.movieDao().getMovieCount(movie.getId());
                if(movieCount<1){
                    /* Add movie to the favourites */
                    mDb.movieDao().insertFavouriteMovie(movie);
                    Log.d(TAG, "Movie added to favourites");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MovieDetail.this, "Added to favourites", Toast.LENGTH_SHORT).show();
                            floatingActionButton.setImageResource(R.drawable.ic_favorite_border);
                        }
                    });

                } else {
                    /* Remove movie from the favourites */
                    mDb.movieDao().deleteFavouriteMovie(movie);
                    Log.d(TAG,"Movie removed from favourites");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MovieDetail.this, "Removed from favourites", Toast.LENGTH_SHORT).show();
                            floatingActionButton.setImageResource(R.drawable.ic_favorite);
                        }
                    });
                }

            }
        });
    }

    /**
     * This method sets up a Volley request and sends an HTTPS API Call
     * to TMD server for getting details of Trailers.
     * This method does not parse the response received
     * from the server. On receiving a response, this method calls
     * {@link MovieDetail#loadTrailerUI(String) to parse the response
     * and display icons for trailers
     */
    public void getTrailerInfoFromServer(String id){
        /* Create the TMD API URL */
        Uri builtUri = Uri.parse(Constants.BASE_URL_MOVIES)
                .buildUpon()
                .appendPath(id)
                .appendPath(Constants.TRAILER_ENDPOINT)
                .appendQueryParameter(Constants.QUERY_PARAM_API_KEY, Constants.API_KEY)
                .appendQueryParameter(Constants.QUERY_PARAM_LANGUAGE, Constants.DEFAULT_API_LANGUAGE_PARAMETER)
                .build();
        String url = builtUri.toString();
        Log.d(TAG,"Trailer Url is " + url);

        /* Instantiate Volley RequestQueue */
        RequestQueue queue = Volley.newRequestQueue(this);
        /* Request a string response from the provided URL */
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /* On receiving response, call loadTrailerUI */
                        loadTrailerUI(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"Volley Request Error: " + error.getMessage());
            }
        });
        /* Add the request to the RequestQueue */
        queue.add(stringRequest);
    }

    /**
     * This method parses the JSON response from the server regarding the trailers
     * and creates and displays the UI for trailers. This method is also responsible
     * for setting the onclick listeners on the trailer icons and dispatching the
     * implicit intents.
     * @param serverResponseString String containing the JSON response from the server
     *                             in the form of a string.
     */
    public void loadTrailerUI(String serverResponseString){
        /* All the trailers will be displayed in a LinearLayout nested inside a HorizontalScrollView.
         * Each trailer item has a trailer icon and a trailer name. A separate LinearLayout is created for
         * each trailer item to display them correctly. These separate per item LinearLayouts are then nested
         * inside a single LinearLayout with horizontal orientation, which in turn is nested inside the
         * HorizontalScrollView.
         */

        /* Main LinearLayout nested inside the HorizontalScrollView already defined in the Layout file*/
        LinearLayout linearLayoutTrailers = findViewById(R.id.linear_layout_trailers_main);

        try {
            /* Convert response string into a JSON object*/
            JSONObject response = new JSONObject(serverResponseString);

            /* The JSON response contains several fiels but we are only interested
             * in the "results" JSON array containing details about the trailers
             */
            JSONArray resultsJsonArray = response.getJSONArray("results");

            /* If no trailers are available for the movie, display a message
             * that no trailers are available
             */
            if(resultsJsonArray.length()<1){
                TextView noTrailersAvailableTextView = new TextView(this);
                noTrailersAvailableTextView.setText(R.string.no_trailers_available);
                LinearLayout.LayoutParams noTrailersTextViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int bottomMargin = getResources().getDimensionPixelSize(R.dimen.review_content_bottom_margin);
                noTrailersTextViewLayoutParams.bottomMargin = bottomMargin;
                noTrailersAvailableTextView.setLayoutParams(noTrailersTextViewLayoutParams);
                linearLayoutTrailers.addView(noTrailersAvailableTextView);
            }

            /* Loop through each trailer in the array*/
            for(int i=0;i<resultsJsonArray.length();i++){
                /* Each trailer has several fields but we are only interested in
                 * the name of the trailer and the "key" which is actually
                 * the id of the video on Youtube. So we ignore all the fields
                 * of the trailer object except the "name" and the "key".
                 */
                JSONObject result = resultsJsonArray.getJSONObject(i);
                String trailerKey = result.getString("key");
                String trailerName = result.getString("name");

                /* Since the trailer key contains only the id of the video on the youtube,
                 * we have to build the complete URL ourselves.
                 * Here I am using the base url of youtube + watch_endpoint + query_parameter + trailer key
                 * i.e. https://www.youtube.com/watch?v=<<trailer key>>
                 */
                final Uri youtubeUri = Uri.parse(Constants.YOUTUBE_BASE_URL)
                        .buildUpon()
                        .appendPath(Constants.YOUTUBE_WATCH_ENDPOINT)
                        .appendQueryParameter(Constants.YOUTUBE_QUERY_PARAM, trailerKey)
                        .build();
                final String completeTrailerPath = youtubeUri.toString();

                /* ImageViews for displaying icon for trailer and TextViews for displaying
                 * name of trailer will be created dynamically as we do not know the exact
                 * number of trailers of each movie in advance. So these will be created
                 * dynamically as per the need
                 */
                ImageView trailerImageView = new ImageView(this);
                trailerImageView.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                trailerImageView.setTag(completeTrailerPath);



                TextView trailerNameTextView = new TextView(this);
                trailerNameTextView.setText(trailerName);
                trailerNameTextView.setGravity(Gravity.CENTER_HORIZONTAL);

                /* Since we need two properties of each trailer - icon and name - to be displayed,
                 * we will nest them inside a LinearLayout with vertical orientation so that these
                 * can be displayed nicely - Name of the trailer below the trailer icon.
                 */

                LinearLayout oneTrailerLinearLayout = new LinearLayout(this);
                oneTrailerLinearLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int marginEnd = getResources().getDimensionPixelSize(R.dimen.trailer_image_right_margin);
                linearLayoutParams.setMarginEnd(marginEnd);
                oneTrailerLinearLayout.setLayoutParams(linearLayoutParams);
                oneTrailerLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

                /* Set the height and width of the icon and trailer name views */
                int width = getResources().getDimensionPixelSize(R.dimen.trailer_image_width);
                int height = getResources().getDimensionPixelSize(R.dimen.trailer_image_height);

                /* Add the icon and trailer name views to the "per trailer" LinearLayout */
                oneTrailerLinearLayout.addView(trailerImageView, width, height);
                oneTrailerLinearLayout.addView(trailerNameTextView, width, LinearLayout.LayoutParams.WRAP_CONTENT);

                /* Set the onClickListener on the LinearLayout itself so that the
                 * trailer is launched irrespective of whether the user clicks on the
                 * trailer icon or the trailer name.
                 */
                oneTrailerLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* Dispatch an implicit intent so that user has the choice to
                         * view the trailer either in the youtube native app or a web
                         * browser
                         */
                        Intent trailerIntent = new Intent(Intent.ACTION_VIEW,youtubeUri);
                        if(trailerIntent.resolveActivity(getPackageManager()) != null){
                            startActivity(trailerIntent);
                        } else {
                            Log.e(TAG,"Cannot find any app to view trailer");
                        }
                    }
                });

                /* Add the dynamically created "per trailer" LinearLayout to the main
                 * LinearLayout which is nested inside the HorizontalScrollView in the
                 * layout file
                 */
                linearLayoutTrailers.addView(oneTrailerLinearLayout);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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


    /**
     * This method sets up a Volley request and sends an HTTPS API Call
     * to TMD server for getting details of Reviews.
     * This method does not parse the response received
     * from the server. On receiving a response, this method calls
     * {@link MovieDetail#loadReviewsUI(String) to parse the response
     * and display icons for trailers
     */
    public void getReviewsInfoFromServer(String id){
        /* Create the TMD API URL */
        Uri builtUri = Uri.parse(Constants.BASE_URL_MOVIES)
                .buildUpon()
                .appendPath(id)
                .appendPath(Constants.REVIEWS_ENDPOINT)
                .appendQueryParameter(Constants.QUERY_PARAM_API_KEY, Constants.API_KEY)
                .appendQueryParameter(Constants.QUERY_PARAM_LANGUAGE, Constants.DEFAULT_API_LANGUAGE_PARAMETER)
                .build();
        String url = builtUri.toString();
        Log.d(TAG,"Reviews Url is " + url);

        /* Instantiate Volley RequestQueue */
        RequestQueue queue = Volley.newRequestQueue(this);
        /* Request a string response from the provided URL */
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /* On receiving response, call loadTrailerUI */
                        loadReviewsUI(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"Volley Request Error: " + error.getMessage());
            }
        });
        /* Add the request to the RequestQueue */
        queue.add(stringRequest);
    }


    /**
     * This method parses the JSON response from the server regarding the reviews
     * and creates and displays the UI.
     * @param serverResponseString String containing the JSON response from the server
     *                             in the form of a string.
     */
    public void loadReviewsUI(String serverResponseString){
        /* Each review will contain two textviews - one displaying name of the author
         * and the other one displaying the actual review. I will simply add these
         * textviews to the main linear layout as nothing fancy is required here
         */

        /* Main LinearLayout nested inside the HorizontalScrollView already defined in the Layout file*/
        LinearLayout linearLayoutReviews = findViewById(R.id.linear_layout_reviews_main);

        try {
            /* Convert response string into a JSON object*/
            JSONObject response = new JSONObject(serverResponseString);

            /* The JSON response contains several fiels but we are only interested
             * in the "results" JSON array containing details about the reviews
             */
            JSONArray resultsJsonArray = response.getJSONArray("results");

            /* If no reviews are available, show a message that no reviews are available */
            if(resultsJsonArray.length()<1){
                TextView noReviewsAvailableTextView = new TextView(this);
                noReviewsAvailableTextView.setText(R.string.no_reviews_available);
                LinearLayout.LayoutParams noReviewsTextViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int bottomMargin = getResources().getDimensionPixelSize(R.dimen.review_content_bottom_margin);
                noReviewsTextViewLayoutParams.bottomMargin = bottomMargin;
                noReviewsAvailableTextView.setLayoutParams(noReviewsTextViewLayoutParams);
                linearLayoutReviews.addView(noReviewsAvailableTextView);
            }

            /* Loop through each trailer in the array*/
            for(int i=0;i<resultsJsonArray.length();i++){
                /* Each review has several fields but we are only interested in
                 * the "author" and "content" fields. So we ignore all the fields
                 * of the review object except the "author" and the "content".
                 */
                JSONObject result = resultsJsonArray.getJSONObject(i);
                String reviewAuthorName = "Written by " + result.getString("author");
                String reviewContent = result.getString("content");

                TextView reviewAuthorNameTextView = new TextView(this);
                reviewAuthorNameTextView.setText(reviewAuthorName);
                reviewAuthorNameTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                LinearLayout.LayoutParams authorNameTextViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                reviewAuthorNameTextView.setLayoutParams(authorNameTextViewLayoutParams);
                reviewAuthorNameTextView.setTypeface(reviewAuthorNameTextView.getTypeface(), Typeface.ITALIC);

                TextView reviewContentTextView = new TextView(this);
                reviewContentTextView.setText(reviewContent);
                LinearLayout.LayoutParams reviewContentTextViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int bottomMargin = getResources().getDimensionPixelSize(R.dimen.review_content_bottom_margin);
                reviewContentTextViewLayoutParams.bottomMargin = bottomMargin;
                reviewContentTextView.setLayoutParams(reviewContentTextViewLayoutParams);

                View separatorView = new View(this);
                separatorView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                int separatorViewHeight = getResources().getDimensionPixelSize(R.dimen.review_separator_view_height);
                int separatorViewWidth = getResources().getDimensionPixelSize(R.dimen.review_separator_view_width);
                LinearLayout.LayoutParams separatorViewLayoutParams = new LinearLayout.LayoutParams(separatorViewWidth, separatorViewHeight);
                separatorViewLayoutParams.bottomMargin = bottomMargin;
                separatorView.setLayoutParams(separatorViewLayoutParams);

                linearLayoutReviews.addView(reviewAuthorNameTextView);
                linearLayoutReviews.addView(reviewContentTextView);
                linearLayoutReviews.addView(separatorView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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


}
