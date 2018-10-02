package com.example.irshad.moviesappstage1.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.irshad.moviesappstage1.Models.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM favmovies ORDER BY rating")
    LiveData<List<Movie>> loadAllFavouriteMovies();

    @Query("SELECT * FROM favmovies WHERE id = :id")
    LiveData<Movie> loadFavouriteMovieById(String id);

    @Query("SELECT COUNT(id) FROM favmovies WHERE id = :id")
    int getMovieCount(String id);

    @Insert
    void insertFavouriteMovie(Movie movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(Movie movie);

    @Delete
    void deleteFavouriteMovie(Movie movie);

}
