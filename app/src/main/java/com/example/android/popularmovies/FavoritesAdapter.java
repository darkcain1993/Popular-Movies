package com.example.android.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.popularmovies.Database.MovieEntry;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    //Create variable to hold the list of movies (this will be the data the adapter will use)
    private List<MovieEntry> mMovieEntries;

    public FavoritesAdapter(List<MovieEntry> movieEntries){
        mMovieEntries = movieEntries;
    }

    public List<MovieEntry> getFavorites(){
        return mMovieEntries;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesAdapter.FavoriteViewHolder holder, int position) {

        byte [] poster = mMovieEntries.get(position).getPosterImage();
        String title = mMovieEntries.get(position).getOriginalTitle();
        String plot = mMovieEntries.get(position).getPlotOverView();

        holder.imageItemView.setImageBitmap(convertByteToImage(poster));
        holder.titleTextView.setText(title);
        holder.plotTextView.setText(plot);
    }
    private Bitmap convertByteToImage(byte[] poster){
        return BitmapFactory.decodeByteArray(poster, 0, poster.length);
    }

    @Override
    public int getItemCount() {
        return mMovieEntries.size();
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Establish a context and inflate the view into a viewholder using it's id
        Context context = viewGroup.getContext();
        int layoutID = R.layout.favorites_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean noParentAttachment = false;

        View view = inflater.inflate(layoutID, viewGroup, noParentAttachment);
        FavoriteViewHolder viewHolder = new FavoriteViewHolder(view);
        return viewHolder;

    }

    //This class contains the view holder which will handle all view endpoints
    class FavoriteViewHolder extends RecyclerView.ViewHolder  {
        ImageView imageItemView;
        TextView plotTextView;
        TextView titleTextView;

        private FavoriteViewHolder(View movieView){
            super(movieView);
            imageItemView = movieView.findViewById(R.id.iv_movie_poster1);
            titleTextView = movieView.findViewById(R.id.tv_movie_title1);
            plotTextView = movieView.findViewById(R.id.tv_plot_overview1);
        }
    }

}
