package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

// This class creates the structure of the Movie object, implements parcelable to pass the object data between activities using intents
public class MovieDetails implements Parcelable {

    private String originalTitle;
    private String posterImage;
    private String plotOverView;
    private double userRating;
    private String releaseDate;

    public MovieDetails(){

    }

    public MovieDetails(String originalTitle, String posterImage, String plotOverView, double userRating, String releaseDate){
        this.originalTitle = originalTitle;
        this.posterImage = posterImage;
        this.plotOverView = plotOverView;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    // This method will read in the data from the object
    private MovieDetails(Parcel in){
        originalTitle = in.readString();
        posterImage = in.readString();
        plotOverView = in.readString();
        userRating = in.readDouble();
        releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override // this method "parcels" the object data for transfer between activities
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(originalTitle);
        parcel.writeString(posterImage);
        parcel.writeString(plotOverView);
        parcel.writeDouble(userRating);
        parcel.writeString(releaseDate);
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
}
