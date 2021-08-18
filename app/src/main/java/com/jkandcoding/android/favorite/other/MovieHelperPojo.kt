package com.jkandcoding.android.favorite.other

data class MovieHelperPojo(
    val Title: String,
    val Year: String,
    val imdbID: String,
    val Poster: String
) {
  //  var isFavorite: Boolean = false
    var isInDB: Boolean = false
}
