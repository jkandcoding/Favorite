package com.jkandcoding.android.favorite.network


import com.jkandcoding.android.favorite.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FavoriteApi {

    companion object {
        const val BASE_URL = "http://www.omdbapi.com"
        const val CLIENT_ID = BuildConfig.OMDB_ACCESS_KEY
     }

    //@Headers("apikey: $CLIENT_ID")
    @GET("?apikey=$CLIENT_ID")
    suspend fun searchMoviesByTitle(
        @Query("s") title: String,
    ): Response<MovieResponse>

    @GET("?apikey=$CLIENT_ID")
    suspend fun searchMoviesByImdbID(
        @Query("i") imdbID: String
    ): Response<Movie>  //todo vidi sto ovdje

}