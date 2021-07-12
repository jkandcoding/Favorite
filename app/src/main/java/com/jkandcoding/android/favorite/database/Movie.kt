package com.jkandcoding.android.favorite.database

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val Title: String,
    val Year: String,
    val Released: String?,
    val Runtime: String?,
    val imdbRating: String?,
    val Genre: String?,
    val Plot: String?
) : Parcelable
