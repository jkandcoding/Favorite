package com.jkandcoding.android.favorite.network

import com.jkandcoding.android.favorite.database.MovieDB

data class MovieResponse(
    val Search: List<MovieDB>,
    val totalResults: String,
    val Response: Boolean
)
