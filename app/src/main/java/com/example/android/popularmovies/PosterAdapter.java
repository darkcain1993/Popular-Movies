package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.popularmovies.Utilities.Constants.imageUrl;


public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder> {

    //Create variable to hold the list of movies (this will be the data the adapter will use)
    private List<MovieDetails> mMovieItems;

    private PosterItemClickHandler mClickhandler;

    //This interface holes the onclick listener
    public interface PosterItemClickHandler{
        void onPosterItemClick(MovieDetails movieDetails);
    }


    // This helper method takes in a list of movies and saves it in the above variable, this method will later take in the click listener
    // (this is how all needed parts are initialized)
    public PosterAdapter(List movieDetails, PosterItemClickHandler clickHandler){
        mMovieItems = movieDetails;
        mClickhandler = clickHandler;

    }



    @NonNull
    @Override // This method inflates the view that will be used by the recycler
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        // Establish a context and inflate the view into a viewholder using it's id
        Context context = viewGroup.getContext();
        int layoutID = R.layout.movie_poster;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean noParentAttachment = false;

        View view = inflater.inflate(layoutID, viewGroup, noParentAttachment);
        PosterViewHolder viewHolder = new PosterViewHolder(view);
        return viewHolder;
    }

    @Override // This method extracts data from the movie list, creates an image url, and loads the image into
              // the holders "image view" using Picasso
    public void onBindViewHolder(@NonNull PosterViewHolder holder, int position) {

        // loads the image into the image view holder
        Picasso.get().load(imageUrl + mMovieItems.get(position).getPosterImage()).into(holder.imageItemView);
    }



    @Override // This method tells the adapter how many views it needs to handle
    public int getItemCount() {
        return mMovieItems.size();
    }

    //This class contains the view holder which will handle all view endpoints, have the view holder implement the click listener
    class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageItemView;

        // This method is used for setting the views to the image view and giving the view a click listener
        private PosterViewHolder(View imageView){
            super(imageView);

            imageItemView = imageView.findViewById(R.id.iv_movie_poster);
            imageView.setOnClickListener(this);
        }

        @Override // This method lets the (code? IDE?) know what data to use and code to execute once a specific item is clicked
        public void onClick(View view) {
            int position = getAdapterPosition();
            MovieDetails movieDetails = mMovieItems.get(position);
            mClickhandler.onPosterItemClick(movieDetails);
        }
    }


}

