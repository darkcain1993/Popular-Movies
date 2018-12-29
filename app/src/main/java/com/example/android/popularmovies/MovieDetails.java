package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

// This class creates the structure of the Movie object, implements parcelable to pass the object data between activities using intents
public class MovieDetails implements Parcelable {

    private long movieId;
    private String originalTitle;
    private String posterImage;
    private String plotOverView;
    private double userRating;
    private String releaseDate;
    private String[] videoTrailers;

    public MovieDetails(){

    }

    public MovieDetails(long movieId, String originalTitle, String posterImage, String plotOverView, double userRating, String releaseDate, String[] videoTrailers){
        this.movieId = movieId;
        this.originalTitle = originalTitle;
        this.posterImage = posterImage;
        this.plotOverView = plotOverView;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.videoTrailers = videoTrailers;
    }

    // This method will read in the data from the object
    private MovieDetails(Parcel in){
        movieId = in.readLong();
        originalTitle = in.readString();
        posterImage = in.readString();
        plotOverView = in.readString();
        userRating = in.readDouble();
        releaseDate = in.readString();
        //videoTrailers = in.createStringArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override // this method "parcels" the object data for transfer between activities
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(movieId);
        parcel.writeString(originalTitle);
        parcel.writeString(posterImage);
        parcel.writeString(plotOverView);
        parcel.writeDouble(userRating);
        parcel.writeString(releaseDate);
        //parcel.writeStringArray(videoTrailers);
    }

    // this method will unpack he parcel and store it in the appropriate object
    public static final Parcelable.Creator<MovieDetails> CREATOR = new Parcelable.Creator<MovieDetails>(){
        @Override
        public MovieDetails createFromParcel(Parcel parcel) {

            return new MovieDetails(parcel);
        }

        @Override
        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };

    // create the individual getter and setter methods to create or display the movie object
    public long getMovieId(){
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public String getOriginalTitle(){
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPosterImage() {
        return posterImage;
    }

    public void setPosterImage(String posterImage) {
        this.posterImage = posterImage;
    }

    public String getPlotOverView() {
        return plotOverView;
    }

    public void setPlotOverview(String plotOverView) {
        this.plotOverView = plotOverView;
    }

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String[] getVideoTrailers(){return videoTrailers;}

    public void setVideoTrailers(String[] videoTrailers) {
        this.videoTrailers = videoTrailers;
    }
}
