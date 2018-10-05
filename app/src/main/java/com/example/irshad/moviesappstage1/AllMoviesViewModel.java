package com.example.irshad.moviesappstage1;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.irshad.moviesappstage1.Database.AppDatabase;
import com.example.irshad.moviesappstage1.Models.Movie;

import java.util.List;

public class AllMoviesViewModel extends AndroidViewModel {

    private static final String TAG = AllMoviesViewModel.class.getSimpleName();

    private LiveData<List<Movie>> movieList;

    public AllMoviesViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
        movieList = database.movieDao().loadAllFavouriteMovies();
        Log.d(TAG,"Loading all favourite movies from database actively...");
    }

    public LiveData<List<Movie>> getMovieList(){
        return movieList;
    }
}
