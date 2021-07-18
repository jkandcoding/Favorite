package com.jkandcoding.android.favorite.network

import com.jkandcoding.android.favorite.other.MovieHelperPojo

data class MovieResponse(
    val Search: List<MovieHelperPojo>?,
    val totalResults: String?,
    val Response: String,
    val Error: String?
)
