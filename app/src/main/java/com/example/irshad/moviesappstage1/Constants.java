package com.example.irshad.moviesappstage1;

import java.lang.ref.SoftReference;

public class Constants {

    public static final String BASE_URL_POSTER_PATH = "https://image.tmdb.org/t/p/";
    public static final String POSTER_RESOULTION = "w342";
    public static final String QUERY_PARAM_API_KEY = "api_key";
    public static final String API_KEY = BuildConfig.API_KEY;
    public static final String BASE_URL_MOVIES = "https://api.themoviedb.org/3/movie/";
    public static final String SORT_ORDER_POPULAR = "popular";
    public static final String SORT_ORDER_TOP_RATED = "top_rated";
    public static final String SORT_ORDER_MY_FAVOURITE_MOVIES ="my_favourite_movies";
    public static final String SORT_ORDER_KEY_FOR_STATE_RESTORATION = "sort_order";
    public static final String DEFAULT_SORT_ORDER = SORT_ORDER_POPULAR;
    public static final String TRAILER_ENDPOINT = "videos";
    public static final String REVIEWS_ENDPOINT = "reviews";
    public static final String QUERY_PARAM_LANGUAGE = "language";
    public static final String DEFAULT_API_LANGUAGE_PARAMETER = "en-US";
    public static final String YOUTUBE_BASE_URL = "https://www.youtube.com";
    public static final String YOUTUBE_WATCH_ENDPOINT = "watch";
    public static final String YOUTUBE_QUERY_PARAM = "v";
}
