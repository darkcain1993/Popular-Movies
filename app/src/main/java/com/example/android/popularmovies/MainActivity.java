package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Utilities.JsonUtility;
import com.example.android.popularmovies.Utilities.NetUtilities;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements PosterAdapter.PosterItemClickHandler {


    private static String popularSortLink = "https://api.themoviedb.org/3/discover/movie?api_key={enter key here}&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1";
    private static String topRatingSortLink = "https://api.themoviedb.org/3/movie/top_rated?api_key={enter key here}&language=en-US&page=1";

    private PosterAdapter posterAdapter;

    @BindView(R.id.pb_movie_loading_bar) ProgressBar mMovieProgressbar;
    @BindView(R.id.rv_posters)RecyclerView mPosterRecycViews;
    @BindView(R.id.tv_error_message1) TextView mErrorMessage1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // Create the grid layout and apply it to the recycler view
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        mPosterRecycViews.setLayoutManager(layoutManager);
        mPosterRecycViews.setHasFixedSize(true);

        updateUI(popularSortLink);
    }

    // This method updates the UI based on whether there is a network connection or not.
    public void updateUI(String sortLink){
        if(!isOnline()){

            mErrorMessage1.setVisibility(View.VISIBLE);
            mPosterRecycViews.setVisibility(View.INVISIBLE);
        }else{

            mErrorMessage1.setVisibility(View.INVISIBLE);
            mPosterRecycViews.setVisibility(View.VISIBLE);
            executeTask(sortLink);
        }
    }



    // This creates a task that is run on a separate thread
    public class MovieDBQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMovieProgressbar.setVisibility(View.VISIBLE);
        }

        @Override //Take in the url as an input, store it in a variable, and return the response as a string
        protected String doInBackground(URL... urls) {
            URL searchMovieUrl = urls[0];
            String movieDbSearchResults = null;

            try{
                movieDbSearchResults = NetUtilities.getResponseFromUrl(searchMovieUrl);
            }catch(IOException e){
                return null;
            }

            return movieDbSearchResults;
        }

        @Override //This method accepts the movie website json string and executes the remaining code
        protected void onPostExecute(String movieDbSearchSearchResults) {
            mMovieProgressbar.setVisibility(View.INVISIBLE);

            if(movieDbSearchSearchResults != null && !movieDbSearchSearchResults.equals("")){

                //Parse the JSON string and store in a list of Movie objects
                List<MovieDetails> movieDetailsList = JsonUtility.parseMovieDetailsJson(movieDbSearchSearchResults);
                // display the data
                loadMovieData(movieDetailsList);
            }else{
                //show some kind of error
            }
        }
    }


    // This method takes in a string to build the url and executes the query using the url
    public void executeTask(String webUrlString){

        URL url = NetUtilities.buildUrl(webUrlString);
        new MovieDBQueryTask().execute(url);
    }


    public void loadMovieData(List movieDetailsList){

        //Create the adapter using the MovieDetails lists and apply the adapter to the recycler view
        posterAdapter = new PosterAdapter(movieDetailsList,this);
        mPosterRecycViews.setAdapter(posterAdapter);
    }

    @Override //This method opens the next activity and loads data based on the index passed through from the adapter onClick method
    public void onPosterItemClick(MovieDetails movieDetails) {

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("parcel_data", movieDetails);
        startActivity(intent);
    }

    //Method sourced from https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
    //by user Levit 12/05/14
    //This method checks if there is internet access and returns a boolean
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    @Override //Override this method to inflate the menu resource
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_by, menu);
        return true;
    }

    @Override //Handle clicks on certain menu items. In this case it handles the sorting methods.
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.popular_sort){
            updateUI(popularSortLink);
            return true;
        }else if(id == R.id.rating_sort){
            updateUI(topRatingSortLink);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

