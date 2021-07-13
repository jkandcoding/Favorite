package com.jkandcoding.android.favorite.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieDB(
    val Title: String,
    val Year: String,
    @PrimaryKey
    val imdbID: String,

)
