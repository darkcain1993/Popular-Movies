package com.example.android.popularmovies.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

// step 2
@Dao // stands for data access object. Creates the functionality to modify the the database
public interface MovieDao {

    @Query("SELECT * FROM movie ORDER BY title")
    LiveData<List<MovieEntry>> loadAllMovies();

    @Query("SELECT * FROM movie WHERE id = :id")
    long loadMovieId(long id);

    @Insert
    void insertMovie(MovieEntry movieEntry);

    @Delete
    void deleteMovie(MovieEntry movieEntry);

}
