package com.jkandcoding.android.favorite.network

import android.util.Log
import com.jkandcoding.android.favorite.database.Movie
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val favoriteApi: FavoriteApi
) {
//    suspend fun getMoviesByTitle(title: String): Response<List<Movie>> {
//        return favoriteApi.searchMoviesByTitle(title)
//    }

    suspend fun getMoviesByTitle(title: String): Response<MovieResponse> {
        val responseMovie: Response<MovieResponse> = favoriteApi.searchMoviesByTitle(title)
        Log.d("responseMovie", "Repository - response from api: " + responseMovie)
        return responseMovie
    }

    suspend fun getMovieByImdbID(imdbID: String): Response<Movie> {
        return favoriteApi.searchMoviesByImdbID(imdbID)
    }

}