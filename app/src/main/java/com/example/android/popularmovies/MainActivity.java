package com.example.android.popularmovies;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.example.android.popularmovies.Utilities.Constants.popularSortLink;
import static com.example.android.popularmovies.Utilities.Constants.topRatingSortLink;

public class MainActivity extends AppCompatActivity implements PosterAdapter.PosterItemClickHandler {

   private PosterAdapter posterAdapter;
    private MovieDataBase mDb;
    private FavoritesAdapter favoritesAdapter;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private final String FavoriteViewState = "favorites-view-state";
    private boolean VIEWSTATE1;
    private GridLayoutManager layoutManager1;


    @BindView(R.id.rv_posters)RecyclerView mPosterRecycViews;
    //@BindView(R.id.rv_favorites) RecyclerView mFavRecycViews;
    @BindView(R.id.tv_error_message1) TextView mErrorMessage1;
    @BindView(R.id.tv_error_message2) TextView mErrorMessage2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mDb = MovieDataBase.getInstance(getApplicationContext());

        setRecyclerViews();
        updateUI(popularSortLink);
        loadFavoritesData();





        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("TEST onCreate", "The views may have updated HERE.");
    }


    // This method sets the recycler views for the main screen
    public void setRecyclerViews(){
        // Create the grid layout and apply it to the poster recycler view
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        layoutManager1 = new GridLayoutManager(this,1);
        mPosterRecycViews.setLayoutManager(layoutManager);
        mPosterRecycViews.setHasFixedSize(true);

    }

    // This method updates the UI based on whether there is a network connection or not.
    public void updateUI(String movieLink){
        if(!isOnline()){
            mErrorMessage1.setVisibility(View.VISIBLE);
            mPosterRecycViews.setVisibility(View.INVISIBLE);
        }else{
            mErrorMessage1.setVisibility(View.INVISIBLE);
            mPosterRecycViews.setVisibility(View.VISIBLE);
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
                        mPosterRecycViews.setVisibility(View.VISIBLE);
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
                        mPosterRecycViews.setVisibility(View.INVISIBLE);

                    }


                });
        mRequestQueue.add(jsonObjectRequest);
    }

    public void loadMovieData(List movieDetailsList){
        //Create the adapter using the MovieDetails lists and apply the adapter to the recycler view
        posterAdapter = new PosterAdapter(movieDetailsList, this);
        mPosterRecycViews.setAdapter(posterAdapter);
    }

    // This method views changes in the favorites database and updates it's cooresponding recycler view
    public void loadFavoritesData(){
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                Log.d("TAG", "UPDATE FROM THE DATABASE using livedata in viewmodel");
                favoritesAdapter = new FavoritesAdapter(movieEntries);
            }
        });

    }

    //This method updates the main view to show the favorites list
    public void showFavsList(){

        mPosterRecycViews.setLayoutManager(layoutManager1);
        mPosterRecycViews.setHasFixedSize(false);
        mPosterRecycViews.setAdapter(favoritesAdapter);
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
    protected void onPause() {
        super.onPause();
        editor = prefs.edit();
        editor.putBoolean(FavoriteViewState, VIEWSTATE1);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean favoritesState = prefs.getBoolean(FavoriteViewState, VIEWSTATE1);
        if(favoritesState){
            Log.d("TEST onResume", "The views should have updated HERE.");
            showFavsList();
        }else{
            showFavsList();
        }
    }

}

