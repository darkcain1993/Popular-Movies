package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {


    private List<String> mTrailerTitles;
    private TrailerClickHandler mClickHandler;
    private MovieDetails mMovieItems;

    //This interface holes the onclick listener
    public interface TrailerClickHandler{
        void onTrailerClick(String trailerKey);
    }

    public TrailerAdapter(List<String> trailerTitles, TrailerClickHandler clickHandler, MovieDetails movieDetails){
        mTrailerTitles = trailerTitles;
        mClickHandler = clickHandler;
        mMovieItems = movieDetails;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Establish a context and inflate the view into a viewholder using it's id
        Context context = viewGroup.getContext();
        int layoutID = R.layout.trailer_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean noParentAttachment = false;

        View view = inflater.inflate(layoutID, viewGroup, noParentAttachment);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapter.ViewHolder holder, int position) {
        holder.mTitles.setText(mTrailerTitles.get(position));
    }

    @Override // This method tells the adapter how many views it needs to handle
    public int getItemCount() {
        return mTrailerTitles.size();
    }


    //This class contains the view holder which will handle all view endpoints, have the view holder implement the click listener
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitles;
        ImageButton mPlayButton;

        // This method is used for setting the views to the image view and giving the view a click listener
        private ViewHolder(View view) {
            super(view);

            mTitles = view.findViewById(R.id.tv_trailer_title);
            mPlayButton = view.findViewById(R.id.play_button);
            mPlayButton.setOnClickListener(this);
        }


        @Override
        // This method lets the (code? IDE?) know what data to use and code to execute once a specific item is clicked
        public void onClick(View view) {
            int position = getAdapterPosition();
            String trailerKey = mMovieItems.getVideoTrailers()[position];
            mClickHandler.onTrailerClick(trailerKey);
        }

    }
}
