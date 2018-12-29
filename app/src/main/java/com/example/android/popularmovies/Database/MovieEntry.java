package com.example.android.popularmovies.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

// step 1
@Entity(tableName = "movie")  // Turns the class into an entity used to model a database object (the movie object)
public class MovieEntry {

    //This makes the id the identifying key for each entry
    @PrimaryKey
    @ColumnInfo(name = "id")
    private long movieId;
    @ColumnInfo(name = "title")
    private String originalTitle;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte [] posterImage;
    @ColumnInfo(name = "plot_overview")
    private String plotOverView;
    @ColumnInfo(name = "rating")
    private double userRating; // may need to change to int
    @ColumnInfo(name = "release_date")
    private String releaseDate;

    /*
    @Ignore // this lets Room know that it should use the second constructor to make Fav. Movie objects
    public MovieEntry(String originalTitle, byte [] posterImage, String plotOverView, double userRating, String releaseDate){
        this.originalTitle = originalTitle;
        this.posterImage = posterImage;
        this.plotOverView = plotOverView;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }
    */


    public MovieEntry(long movieId, String originalTitle, byte [] posterImage, String plotOverView, double userRating, String releaseDate){
        this.movieId = movieId;
        this.originalTitle = originalTitle;
        this.posterImage = posterImage;
        this.plotOverView = plotOverView;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }


    // These are the getter and setter methods for the database
    public long getMovieId(){
        return movieId;
    }

    public String getOriginalTitle(){
        return originalTitle;
    }

    public byte[] getPosterImage() {
        return posterImage;
    }

    public String getPlotOverView() {
        return plotOverView;
    }

    public double getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

}
