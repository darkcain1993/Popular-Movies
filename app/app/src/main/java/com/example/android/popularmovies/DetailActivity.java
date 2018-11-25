package com.example.android.popularmovies;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popularmovies.Utilities.Constants.imageUrl;
import static com.example.android.popularmovies.Utilities.Constants.imageUrl1;

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.tv_movie_title) TextView movieTitle;
    @BindView(R.id.iv_movie_poster) ImageView moviePoster;
    @BindView(R.id.tv_plot_overview) TextView plotOverView;
    @BindView(R.id.tv_user_rating) TextView userRating;
    @BindView(R.id.tv_release_date) TextView releaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        String parcelData = DetailActivity.this.getString(R.string.parcel_data);
        MovieDetails movieDetails = getIntent().getParcelableExtra(parcelData);

        updateUI(movieDetails);
    }

    public void updateUI(MovieDetails movieDetails){
        movieTitle.setText(movieDetails.getOriginalTitle());
        plotOverView.setText(movieDetails.getPlotOverView());
        userRating.setText(String.valueOf(movieDetails.getUserRating()));
        releaseDate.setText(movieDetails.getReleaseDate());


        // Loads the image into the image view holder
        Picasso.get().load(imageUrl1 + movieDetails.getPosterImage()).into(moviePoster);
    }
}
