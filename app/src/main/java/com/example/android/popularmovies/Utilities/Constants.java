package com.example.android.popularmovies.Utilities;

import com.example.android.popularmovies.BuildConfig;

// this class is used to hold constants that may be used in multiple places.
public abstract class Constants {

    private static String apiKey = BuildConfig.APIKey;
    public static final String popularSortLink = "https://api.themoviedb.org/3/movie/popular?api_key=" + apiKey;
    public static final String topRatingSortLink = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + apiKey;
    public static final String imageUrl1 = "http://image.tmdb.org/t/p/" + "w780";
    public static final String imageUrl = "http://image.tmdb.org/t/p/" + "w342" ;
}
