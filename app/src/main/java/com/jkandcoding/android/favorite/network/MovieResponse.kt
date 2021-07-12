package com.jkandcoding.android.favorite.network

import com.jkandcoding.android.favorite.database.Movie
import java.lang.reflect.Array

data class MovieResponse(
    val Search: List<Movie>,
    val totalResults: String,
    val Response: Boolean
)
