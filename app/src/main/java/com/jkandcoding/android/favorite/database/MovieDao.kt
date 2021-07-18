package com.jkandcoding.android.favorite.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoview(movie: Movie)

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("DELETE FROM movies WHERE imdbID = :imdbID")
    suspend fun deleteMovieWithIMDBID(imdbID: String)

    @Query("SELECT * FROM movies")
    fun selectAllFavorites(): LiveData<List<Movie>>

    @Query("SELECT COUNT(*) FROM movies WHERE imdbID = :imdbID")
   suspend fun isMovieInDb(imdbID: String): Int
}