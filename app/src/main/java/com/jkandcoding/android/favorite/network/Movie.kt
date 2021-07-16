package com.jkandcoding.android.favorite.network

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "movies")
data class Movie(
    val Title: String,
    val Year: String,
    val Released: String?,
    val Runtime: String?,
    val imdbRating: String?,
    @PrimaryKey
    val imdbID: String,
    val Genre: String?,
    val Plot: String?
) : Parcelable
