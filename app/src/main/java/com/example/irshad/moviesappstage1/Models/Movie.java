package com.example.irshad.moviesappstage1.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "favmovies")
public class Movie implements Parcelable {
    @PrimaryKey
    @NonNull
    private String id;
    private String title;
    private double rating;
    private double popularity;
    private String thumbnailPath;
    private String backdropPath;
    private String originalTitle;
    private String movieOverview;
    private String releaseDate;

    /* Constructor with id */
    public Movie(String id, String title, double rating, double popularity, String thumbnailPath, String backdropPath, String originalTitle, String movieOverview, String releaseDate) {
        this.id = id;
        this.title = title;
        this.rating = rating;
        this.popularity = popularity;
        this.thumbnailPath = thumbnailPath;
        this.backdropPath = backdropPath;
        this.originalTitle = originalTitle;
        this.movieOverview = movieOverview;
        this.releaseDate = releaseDate;
    }

    @Ignore
    /* Constructor without id */
    public Movie(String title, double rating, double popularity, String thumbnailPath, String backdropPath, String originalTitle, String movieOverview, String releaseDate) {
        this.title = title;
        this.rating = rating;
        this.popularity = popularity;
        this.thumbnailPath = thumbnailPath;
        this.backdropPath = backdropPath;
        this.originalTitle = originalTitle;
        this.movieOverview = movieOverview;
        this.releaseDate = releaseDate;
    }

    @Ignore
    /* Empty Constructor */
    public Movie() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getMovieOverview() {
        return movieOverview;
    }

    public void setMovieOverview(String movieOverview) {
        this.movieOverview = movieOverview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Ignore
    public Movie(Parcel parcel){
        id = parcel.readString();
        title = parcel.readString();
        rating = parcel.readDouble();
        popularity = parcel.readDouble();
        thumbnailPath = parcel.readString();
        backdropPath = parcel.readString();
        originalTitle = parcel.readString();
        movieOverview = parcel.readString();
        releaseDate = parcel.readString();
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeDouble(rating);
        dest.writeDouble(popularity);
        dest.writeString(thumbnailPath);
        dest.writeString(backdropPath);
        dest.writeString(originalTitle);
        dest.writeString(movieOverview);
        dest.writeString(releaseDate);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[0];
        }
    };
}
