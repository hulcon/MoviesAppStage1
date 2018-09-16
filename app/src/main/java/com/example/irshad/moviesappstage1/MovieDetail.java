package com.example.irshad.moviesappstage1;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.irshad.moviesappstage1.Models.Movie;
import com.squareup.picasso.Picasso;


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
        /* Get Movie details from the parcelable extra "movie" object */
        Movie movie = getIntent().getParcelableExtra(MovieAdapter.EXTRA_MOVIE);
        displayMovieDetails(movie);
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


}
