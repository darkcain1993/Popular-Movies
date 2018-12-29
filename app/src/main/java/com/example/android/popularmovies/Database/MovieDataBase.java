package com.example.android.popularmovies.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

//step 3
// Creates an instance of the database or a connection to the already existing database
@Database(entities = {MovieEntry.class}, version = 2, exportSchema = false)
public abstract class MovieDataBase extends RoomDatabase {

    private static final String LOG_TAG = MovieDataBase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "Favorite_Movies";
    private static MovieDataBase sInstance;

    public static MovieDataBase getInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        MovieDataBase.class,
                        MovieDataBase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract MovieDao movieDao();
}
