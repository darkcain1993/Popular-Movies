package com.example.android.popularmovies.Utilities;



import com.example.android.popularmovies.MovieDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtility {

    // This method will take in the JSON string and parse it
    public static List<MovieDetails> parseMovieDetailsJson(JSONObject movieInformation){

        try{
            // Create the JSON Movie List Object that contains the movie information for multiple movies
            //JSONObject movieInformation = new JSONObject(json);

            // The JSON object above contains an array of movie information, so create an array to store it
            JSONArray moviesArray = movieInformation.getJSONArray("results");

            // Loop through the movie array and separate it into individual JSON Objects
            List<JSONObject> jsonObjectList = new ArrayList<>();
            for(int i=0; i<moviesArray.length(); i++){

                jsonObjectList.add(moviesArray.getJSONObject(i));
            }

            //obtain size of the object list
            int length = jsonObjectList.size();

            // Loop through the object list and store the information in individual movie information lists
            List<String> originalTitle = new ArrayList<>();
            List<String> posterImage = new ArrayList<>();
            List<String> plotOverView = new ArrayList<>();
            List<Double> userRating = new ArrayList<>();
            List<String> releaseDate = new ArrayList<>();

            for(int i=0; i<jsonObjectList.size(); i++){

                originalTitle.add(jsonObjectList.get(i).getString("original_title"));
                posterImage.add(jsonObjectList.get(i).getString("poster_path"));
                plotOverView.add(jsonObjectList.get(i).getString("overview"));
                userRating.add(jsonObjectList.get(i).getDouble("vote_average"));
                releaseDate.add(jsonObjectList.get(i).getString("release_date"));
            }

            // Create a list of Movie Details and return it
            return createMovieList(length, originalTitle, posterImage, plotOverView, userRating, releaseDate);


        }catch(JSONException e){
            e.printStackTrace();
        }

        return null;
    }

    // This method is used to create the list of Movie Details pulled from the individual movie information lists
    private static List<MovieDetails> createMovieList(Integer length, List originalTitle, List posterImage, List plotOverView, List userRating, List releaseDate){

        List<MovieDetails> movieDetailsList = new ArrayList<>();

        for(int i=0; i<length; i++){
            MovieDetails movieDetails = new MovieDetails();
            movieDetails.setOriginalTitle(originalTitle.get(i).toString());
            movieDetails.setPosterImage(posterImage.get(i).toString());
            movieDetails.setPlotOverview(plotOverView.get(i).toString());
            movieDetails.setUserRating(Double.parseDouble(userRating.get(i).toString()));
            movieDetails.setReleaseDate(releaseDate.get(i).toString());

            movieDetailsList.add(movieDetails);
        }

        return movieDetailsList;

    }
}
