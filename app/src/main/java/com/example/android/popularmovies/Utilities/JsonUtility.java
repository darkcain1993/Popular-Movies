package com.example.android.popularmovies.Utilities;



import com.example.android.popularmovies.MovieDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtility {

    // This method will take in the JSON object response and parse it
    public static List<MovieDetails> parseMovieDetailsJson(JSONObject movieInformation){

        try{

            // The JSON object contains an array of movie information, so create an array to store it
            JSONArray moviesArray = movieInformation.getJSONArray("results");

            // Loop through the movie array and separate it into individual JSON Objects
            List<JSONObject> jsonObjectList = new ArrayList<>();
            for(int i=0; i<moviesArray.length(); i++){

                jsonObjectList.add(moviesArray.getJSONObject(i));
            }

            //obtain size of the object list
            int length = jsonObjectList.size();

            // Loop through the object list and store the information in individual movie information lists
            List<Long> movieId = new ArrayList<>();
            List<String> originalTitle = new ArrayList<>();
            List<String> posterImage = new ArrayList<>();
            List<String> plotOverView = new ArrayList<>();
            List<Double> userRating = new ArrayList<>();
            List<String> releaseDate = new ArrayList<>();

            for(int i=0; i<length; i++){

                movieId.add(jsonObjectList.get(i).getLong("id"));
                originalTitle.add(jsonObjectList.get(i).getString("original_title"));
                posterImage.add(jsonObjectList.get(i).getString("poster_path"));
                plotOverView.add(jsonObjectList.get(i).getString("overview"));
                userRating.add(jsonObjectList.get(i).getDouble("vote_average"));
                releaseDate.add(jsonObjectList.get(i).getString("release_date"));
            }

            // Create a list of Movie Details and return it
            return createMovieList(length, movieId, originalTitle, posterImage, plotOverView, userRating, releaseDate);


        }catch(JSONException e){
            e.printStackTrace();
        }

        return null;
    }

    // This method is used to create the list of Movie Details pulled from the individual movie information lists
    private static List<MovieDetails> createMovieList(Integer length, List movieId, List originalTitle, List posterImage, List plotOverView, List userRating, List releaseDate){

        List<MovieDetails> movieDetailsList = new ArrayList<>();

        for(int i=0; i<length; i++){
            MovieDetails movieDetails = new MovieDetails();
            movieDetails.setMovieId(Long.parseLong(movieId.get(i).toString()));
            movieDetails.setOriginalTitle(originalTitle.get(i).toString());
            movieDetails.setPosterImage(posterImage.get(i).toString());
            movieDetails.setPlotOverview(plotOverView.get(i).toString());
            movieDetails.setUserRating(Double.parseDouble(userRating.get(i).toString()));
            movieDetails.setReleaseDate(releaseDate.get(i).toString());

            movieDetailsList.add(movieDetails);
        }

        return movieDetailsList;

    }


    public static List<String> parseTrailersJson(JSONObject trailerInfo, MovieDetails movieDetails){

        try{
            // The JSON object contains an array of trailer information, so create an array to store it
            JSONArray trailersArray = trailerInfo.getJSONArray("results");

            // Loop through the trailer array and separate it into individual JSON Objects
            List<JSONObject> jsonObjectList = new ArrayList<>();

            for(int i=0; i<trailersArray.length(); i++){
                jsonObjectList.add(trailersArray.getJSONObject(i));
            }

            //obtain size of the object list
            int length = jsonObjectList.size();

            // Loop through the object list and store the information in a string array
            String[] trailerKeys = new String[length];
            List<String> trailerNames = new ArrayList<>();

            for(int i=0; i<length; i++){
                trailerKeys[i] = jsonObjectList.get(i).getString("key");
                trailerNames.add(jsonObjectList.get(i).getString("name"));
            }

            movieDetails.setVideoTrailers(trailerKeys);


            return trailerNames;

        }catch(JSONException e){
        e.printStackTrace();
        }

        return null;
    }

    public static List<String> parseReviewsJson(JSONObject reviewInfo){

        try{
            // The JSON object contains an array of trailer information, so create an array to store it
            JSONArray reviewsArray = reviewInfo.getJSONArray("results");

            // Loop through the reviews array and separate it into individual JSON Objects
            List<JSONObject> jsonObjectList = new ArrayList<>();

            for(int i=0; i<reviewsArray.length(); i++){
                jsonObjectList.add(reviewsArray.getJSONObject(i));
            }

            //obtain size of the object list
            int length = jsonObjectList.size();

            // Loop through the object list and store the information in a string array
            List<String> reviews = new ArrayList<>();

            for(int i=0; i<length; i++){
                reviews.add(jsonObjectList.get(i).getString("content"));
            }

            return reviews;

        }catch(JSONException e){
            e.printStackTrace();
        }

        return null;
    }

}
