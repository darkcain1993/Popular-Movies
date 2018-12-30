package com.example.android.popularmovies.Utilities;

import com.example.android.popularmovies.BuildConfig;

// this class is used to hold constants that may be used in multiple places.
public abstract class Constants {

    private static final String apiKey = BuildConfig.APIKey;
    public static final String popularSortLink = "https://api.themoviedb.org/3/movie/popular?api_key=" + "Enter Key Here";
    public static final String topRatingSortLink = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + "Enter Key Here";
    public static final String tLink1 = "https://api.themoviedb.org/3/movie/";
    public static final String tLink2 = "/videos?api_key=" + "Enter Key Here";
    public static final String rLink1 = "https://api.themoviedb.org/3/movie/";
    public static final String rLink2 = "/reviews?api_key=" + "Enter Key Here";
    public static final String imageUrl1 = "http://image.tmdb.org/t/p/" + "w780";
    public static final String imageUrl = "http://image.tmdb.org/t/p/" + "w342" ;
    public static final String trailerVideo = "https://www.youtube.com/watch?v=";

}
