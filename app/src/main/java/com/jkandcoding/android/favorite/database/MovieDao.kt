package com.jkandcoding.android.favorite.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jkandcoding.android.favorite.network.Movie

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoview(movieDB: Movie)

    @Delete
    suspend fun deleteMovie(movieDB: Movie)

    @Query("SELECT * FROM movies")
    fun selectAllFavorites(): LiveData<List<Movie>>

    @Query("SELECT COUNT(*) FROM movies WHERE imdbID = :imdbID")
   suspend fun isMovieInDb(imdbID: String): Int
}