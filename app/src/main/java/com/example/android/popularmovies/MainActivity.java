package com.example.android.popularmovies;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.popularmovies.Database.MovieDataBase;
import com.example.android.popularmovies.Database.MovieEntry;
import com.example.android.popularmovies.Utilities.AppExecutors;
import com.example.android.popularmovies.Utilities.JsonUtility;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popularmovies.Utilities.Constants.FAVORITES_KEY;
import static com.example.android.popularmovies.Utilities.Constants.FAVORITES_VIEW_STATE;
import static com.example.android.popularmovies.Utilities.Constants.RECYCLER_STATE;
import static com.example.android.popularmovies.Utilities.Constants.popularSortLink;
import static com.example.android.popularmovies.Utilities.Constants.topRatingSortLink;

public class MainActivity extends AppCompatActivity implements PosterAdapter.PosterItemClickHandler, FavoritesAdapter.FavoritesClickHandler {

    private PosterAdapter posterAdapter;
    private FavoritesAdapter favoritesAdapter;
    private GridLayoutManager layoutManager;
    private GridLayoutManager layoutManager1;
    private MovieDataBase mDb;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private boolean VIEWSTATE1;
    private ArrayList favListState;
    private Parcelable mRecyclerState = null;
    private boolean favoritesState;

    private List<MovieEntry> movies;



    @BindView(R.id.rv_posters)RecyclerView mRecyclerViews;
    @BindView(R.id.tv_error_message1) TextView mErrorMessage1;
    @BindView(R.id.tv_error_message2) TextView mErrorMessage2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mDb = MovieDataBase.getInstance(getApplicationContext());


        loadFavoritesData();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Log.d("TEST onCreate", "The views may have updated HERE.");

        if(savedInstanceState != null){
            favoritesState = prefs.getBoolean(FAVORITES_VIEW_STATE, VIEWSTATE1);

            if(favoritesState){
                //Log.d("TEST onCreate", "The views may have updated HERE.");
                favListState = savedInstanceState.getParcelableArrayList(FAVORITES_KEY);
                //Log.d("TEST onCreate", favListState.get(0).toString());
                //movies = favListState;
                favoritesAdapter = new FavoritesAdapter(favListState, MainActivity.this);
                //favoritesAdapter.setFavorites(favListState);
                showFavsList();
                VIEWSTATE1 = true;

            }else{
                Log.d("TEST onCreate", "The views SADLY may have updated HERE.");
                updateUI(popularSortLink);
            }
        }else{
            updateUI(popularSortLink);
        }
    }


    // This method sets the recycler views for the main screen
    public void setRecyclerViews(){
        // Create the grid layout and apply it to the poster recycler view
        layoutManager = new GridLayoutManager(this,3);
        if(mRecyclerState != null){
            layoutManager.onRestoreInstanceState(mRecyclerState);
        }
        mRecyclerViews.setLayoutManager(layoutManager);
        mRecyclerViews.setHasFixedSize(true);

    }

    // This method updates the UI based on whether there is a network connection or not.
    public void updateUI(String movieLink){
        if(!isOnline()){
            mErrorMessage1.setVisibility(View.VISIBLE);
            mRecyclerViews.setVisibility(View.INVISIBLE);
        }else{
            mErrorMessage1.setVisibility(View.INVISIBLE);
            mRecyclerViews.setVisibility(View.VISIBLE);
            startApp(movieLink);
        }
    }

    //Information sourced from https://developer.android.com/training/volley/requestqueue
    //09/11/18
    // This method makes a network request using Androids Volley mechanisms to retrieve the json data
    private void startApp(String movieLink){
        RequestQueue mRequestQueue;
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);
        // Start the queue
        mRequestQueue.start();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, movieLink, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        setRecyclerViews();
                        mErrorMessage2.setVisibility(View.INVISIBLE);
                        mRecyclerViews.setVisibility(View.VISIBLE);
                        VIEWSTATE1 = false;

                        //Parse the JSON string and store in a list of Movie objects
                        List<MovieDetails> movieDetailsList = JsonUtility.parseMovieDetailsJson(response);

                        // display the data
                        loadMovieData(movieDetailsList);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.i("TAG", error.toString());
                        mErrorMessage2.setVisibility(View.VISIBLE);
                        mRecyclerViews.setVisibility(View.INVISIBLE);
                        VIEWSTATE1 = false;
                    }
                });
        mRequestQueue.add(jsonObjectRequest);
    }

    public void loadMovieData(List movieDetailsList){
        //Create the adapter using the MovieDetails lists and apply the adapter to the recycler view
        posterAdapter = new PosterAdapter(movieDetailsList, this);
        mRecyclerViews.setAdapter(posterAdapter);
    }

    // This method views changes in the favorites database and updates it's cooresponding recycler view
    public void loadFavoritesData(){
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                Log.d("TAG", "UPDATE FROM THE DATABASE using livedata in viewmodel");
                favoritesAdapter = new FavoritesAdapter(movieEntries, MainActivity.this);
                favListState = (ArrayList)(movieEntries);
            }
        });
    }
    public void updateFavoritesData(){
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                Log.d("TAG Update", "UPDATE FROM THE DATABASE using livedata in viewmodel");
                mRecyclerViews.setLayoutManager(layoutManager1);
                mRecyclerViews.setHasFixedSize(false);
                mRecyclerViews.setAdapter(favoritesAdapter);
            }
        });

    }

    //This method updates the main view to show the favorites list
    public void showFavsList(){

        layoutManager1 = new GridLayoutManager(this,1);
        if(mRecyclerState != null){
            layoutManager1.onRestoreInstanceState(mRecyclerState);
        }
        mRecyclerViews.setLayoutManager(layoutManager1);
        mRecyclerViews.setHasFixedSize(false);
        mRecyclerViews.setAdapter(favoritesAdapter);
        mErrorMessage1.setVisibility(View.INVISIBLE);
        VIEWSTATE1 = true;
    }

    @Override //This method opens the next activity and loads data based on the index passed through from the adapter onClick method
    public void onPosterItemClick(MovieDetails movieDetails) {

        String parcelData = MainActivity.this.getString(R.string.parcel_data);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(parcelData, movieDetails);
        startActivity(intent);
    }

    @Override// This method is supposed to delete  a database item and update the UI. Doesn't currently work so set button invisible
    public void onFavoritesClick(final int adapterPosition) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<MovieEntry> movieEntries = favoritesAdapter.getFavorites();
                //mDb.movieDao().deleteMovie(movieEntries.get(adapterPosition));
                //mRecyclerViews.setAdapter(favoritesAdapter);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //showFavsList();
                        //mRecyclerViews.setAdapter(favoritesAdapter);
                        //updateFavoritesData();
                    }
                });
            }
        });
    }

    //Information sourced from https://developer.android.com/training/monitoring-device-state/connectivity-monitoring
    //05/08/18
    //This method checks if there is internet access and returns a boolean
    public boolean isOnline() {

        Context context = MainActivity.this;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
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
        }else if(id == R.id.favorites_sort){
            // display the favorites list
            showFavsList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        favoritesState = prefs.getBoolean(FAVORITES_VIEW_STATE, VIEWSTATE1);
        //Log.d("TEST", String.valueOf(favoritesState));
        if(favoritesState){
            mRecyclerState = layoutManager1.onSaveInstanceState();
            outState.putParcelable(RECYCLER_STATE, mRecyclerState);
            outState.putParcelableArrayList(FAVORITES_KEY, favListState);
        }else{
            //this will save the scroll position on the recyclerView
            mRecyclerState = layoutManager.onSaveInstanceState();
            outState.putParcelable(RECYCLER_STATE, mRecyclerState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);



    }

    @Override
    protected void onPause() {
        super.onPause();
        editor = prefs.edit();
        editor.putBoolean(FAVORITES_VIEW_STATE, VIEWSTATE1);
        editor.apply();
    }
}
