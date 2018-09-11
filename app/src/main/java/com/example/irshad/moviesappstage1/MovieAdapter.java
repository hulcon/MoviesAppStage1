package com.example.irshad.moviesappstage1;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;


import com.example.irshad.moviesappstage1.Models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private static final String TAG = "MovieAdapter";
    private final ArrayList<Movie> movieArrayList;
    private LayoutInflater mInflater;


    /* Constructor */
    public MovieAdapter(Context context, ArrayList<Movie> movies){
        mInflater = LayoutInflater.from(context);
        this.movieArrayList = movies;
    }

    /**
     * This method clears the data from the movie list and renders it empty.
     * This is useful when changing from one sort order to another.
     * For example, if we switch to top_rated sort order from popular sort order
     * the data will append to the list. Using this method, we first clear the
     * old list and then add the new movie list.
     */
    public void clearAllData(){
        this.movieArrayList.clear();
        this.movieArrayList.trimToSize();
    }

    @NonNull
    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mItemView = mInflater.inflate(R.layout.movie_item,viewGroup,false);
        return new MovieViewHolder(mItemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.MovieViewHolder movieViewHolder, int i) {

        float rating = (float) movieArrayList.get(i).getRating();
        movieViewHolder.ratingBarMovieRating.setRating(rating);
        String posterPath = Constants.BASE_URL_POSTER_PATH + Constants.POSTER_RESOULTION + movieArrayList.get(i).getThumbnailPath();
        Log.d(TAG,"Poster Path is: " + posterPath);
        Picasso.get().load(posterPath).placeholder(R.drawable.ic_movie_black_342dp).into(movieViewHolder.imageViewMovieThumbnail);
    }

    @Override
    public int getItemCount() {
        if(movieArrayList==null){
            return 0;
        }
        return movieArrayList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final RatingBar ratingBarMovieRating;
        public final ImageView imageViewMovieThumbnail;
        final MovieAdapter mAdapter;
        public MovieViewHolder(@NonNull View itemView, MovieAdapter adapter) {
            super(itemView);
            ratingBarMovieRating = itemView.findViewById(R.id.ratingBar_movie_item_ratingBar);
            imageViewMovieThumbnail = itemView.findViewById(R.id.imageView_movie_item_movie_thumbnail);
            this.mAdapter = adapter;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent intent = new Intent(context, MovieDetail.class);
            intent.putExtra(MovieDetail.EXTRA_MOVIE_ID,movieArrayList.get(getAdapterPosition()).getId());
            context.startActivity(intent);
        }
    }
}
