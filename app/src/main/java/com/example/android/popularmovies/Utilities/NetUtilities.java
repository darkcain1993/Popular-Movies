package com.example.android.popularmovies.Utilities;



import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
// This file is no longer used. Kept here for knowledge only.
// This class will be used to make requests to through networking activities using Urls
public class NetUtilities {

    // This method will be used to build urls using string input
    public static URL buildUrl(String theMovieDbLink){
        URL url = null;

        try{
            url = new URL(theMovieDbLink);
        }catch(MalformedURLException e){
            return null;
        }

        return url;
    }

    // cites this method later (from Udacity project)
    // This method will will be used to pull data from the url created using the previous method
    // Throwing the exception with the method prevents needs for exception in try/catch
    public static String getResponseFromUrl(URL url) throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try{
            // Read in each element from the connection and return to the desired source
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }

        }finally {
            // always disconnect after completing network task to prevent unintended issues
            urlConnection.disconnect();
        }
    }
}
