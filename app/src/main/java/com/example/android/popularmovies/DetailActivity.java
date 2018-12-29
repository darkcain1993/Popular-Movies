package com.example.android.popularmovies;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popularmovies.Utilities.Constants.imageUrl1;
import static com.example.android.popularmovies.Utilities.Constants.rLink1;
import static com.example.android.popularmovies.Utilities.Constants.rLink2;
import static com.example.android.popularmovies.Utilities.Constants.tLink1;
import static com.example.android.popularmovies.Utilities.Constants.tLink2;
import static com.example.android.popularmovies.Utilities.Constants.trailerVideo;


public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerClickHandler {
    @BindView(R.id.tv_movie_title) TextView movieTitle;
    @BindView(R.id.iv_movie_poster) ImageView moviePoster;
    @BindView(R.id.tv_plot_overview) TextView plotOverView;
    @BindView(R.id.tv_user_rating) TextView userRating;
    @BindView(R.id.tv_release_date) TextView releaseDate;
    @BindView(R.id.ib_favorite) ImageButton favoritesButton;
    @BindView(R.id.rv_movie_trailer) RecyclerView trailerView;
    @BindView(R.id.lv_reviews) ListView reviewsView;


    private MovieDataBase mDb;
    private long MOVIE_ID;
    private MovieDetails movieDetails;
    private boolean FAV;
    private TrailerAdapter trailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        mDb = MovieDataBase.getInstance(getApplicationContext());

        String parcelData = DetailActivity.this.getString(R.string.parcel_data);
        movieDetails = getIntent().getParcelableExtra(parcelData);

        String movieID = Long.toString(movieDetails.getMovieId());
        //Log.d("ID", movieID);

        loadTrailersAndReviews(tLink1+movieID+tLink2, rLink1+movieID+rLink2);
        updateUI(movieDetails);
        updateButton();

    }

    public void updateUI(MovieDetails movieDetails){
        movieTitle.setText(movieDetails.getOriginalTitle());
        plotOverView.setText(movieDetails.getPlotOverView());
        userRating.setText(String.valueOf(movieDetails.getUserRating()));
        releaseDate.setText(movieDetails.getReleaseDate());
        MOVIE_ID = movieDetails.getMovieId();

                // Loads the image into the image view holder
        Picasso.get().load(imageUrl1 + movieDetails.getPosterImage()).into(moviePoster);
    }

    public void updateButton(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d("TAG", "ACTIVELY QUERYING THE DATABASE");
                final Long ans = mDb.movieDao().loadMovieId(movieDetails.getMovieId());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(ans !=0){
                            FAV = true;
                            favoritesButton.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_on));
                        }else if(ans == 0){
                            FAV = false;
                            favoritesButton.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_off));
                        }
                    }
                });
            }
        });
    }

    //step 4
    // This method will add the movie to the database as an entry (edit name since it deletes also)
    public void addToFavorites(View view){

        // add a check
        String title = movieTitle.getText().toString();
        byte [] poster = convertImageToByte(moviePoster);
        String plot = plotOverView.getText().toString();
        double rating = Double.parseDouble(userRating.getText().toString());
        String release = releaseDate.getText().toString();

        if(FAV){
            final MovieEntry movieEntry = new MovieEntry(MOVIE_ID, title, poster, plot, rating, release);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().deleteMovie(movieEntry);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateButton();
                        }
                    });
                }
            });

        }else if(!FAV){
            final MovieEntry movieEntry = new MovieEntry(MOVIE_ID, title, poster, plot, rating, release);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().insertMovie(movieEntry);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateButton();
                        }
                    });
                }
            });
        }
    }

    //This helper method converts the image in the image view into a byte[], which the database can save
    private static byte[] convertImageToByte(ImageView imageView){

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    //Information sourced from https://developer.android.com/training/volley/requestqueue
    //09/11/18
    // This method makes a network request using Androids Volley mechanisms to retrieve the json data
    private void loadTrailersAndReviews(String trailerLink, String reviewLink){
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
                (Request.Method.GET, trailerLink, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        //mErrorMessage2.setVisibility(View.INVISIBLE);
                        //mPosterRecycViews.setVisibility(View.VISIBLE);

                        //Parse the JSON string and store in a list of Movie objects
                        List<String> trailers = JsonUtility.parseTrailersJson(response, movieDetails);

                        // display the data
                        loadTrailerData(trailers);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.i("TAG", error.toString());
                        //mErrorMessage2.setVisibility(View.VISIBLE);
                        //mPosterRecycViews.setVisibility(View.INVISIBLE);

                    }


                });
        mRequestQueue.add(jsonObjectRequest);

        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest
                (Request.Method.GET, reviewLink, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        //mErrorMessage2.setVisibility(View.INVISIBLE);
                        //mPosterRecycViews.setVisibility(View.VISIBLE);

                        //Parse the JSON string and store in a list of Movie objects
                        List<String> reviews = JsonUtility.parseReviewsJson(response);

                        // display the data
                        loadReviewData(reviews);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.i("TAG", error.toString());
                        //mErrorMessage2.setVisibility(View.VISIBLE);
                        //mPosterRecycViews.setVisibility(View.INVISIBLE);

                    }


                });
        mRequestQueue.add(jsonObjectRequest1);
    }

    public void loadTrailerData(List<String> trailers){

        // Create the linear layout and apply it to the favorites recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        trailerView.setLayoutManager(layoutManager);
        trailerAdapter = new TrailerAdapter(trailers, this, movieDetails);
        trailerView.setAdapter(trailerAdapter);
    }

    public void loadReviewData(List<String> reviews){

        // Create the linear layout and apply it to the favorites recycler view
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, reviews);
        reviewsView.setAdapter(adapter);
    }

    @Override
    public void onTrailerClick(String trailerKey) {
        //start link loading intent
        String videoLink = trailerVideo + trailerKey;
        Uri video = Uri.parse(videoLink);
        Intent intent = new Intent(Intent.ACTION_VIEW, video);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }
}
