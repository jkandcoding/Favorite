package com.jkandcoding.android.favorite.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoview(movieDB: MovieDB)

    @Delete
    suspend fun deleteMovie(movieDB: MovieDB)

    @Query("SELECT * FROM movies")
    fun selectAllFavorites(): LiveData<List<MovieDB>>
}