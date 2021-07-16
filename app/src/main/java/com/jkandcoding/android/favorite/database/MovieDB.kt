package com.jkandcoding.android.favorite.database

data class MovieDB(
    val Title: String,
    val Year: String,
    val imdbID: String,
) {
    var isFavorite: Boolean = false
}
