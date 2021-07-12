package com.jkandcoding.android.favorite.network


import com.jkandcoding.android.favorite.database.Movie
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface FavoriteApi {

    companion object {
        const val BASE_URL = "http://www.omdbapi.com"
       // const val CLIENT_ID = BuildConfig.OMDB_ACCESS_KEY
        const val CLIENT_ID = "6817b1f"
        //const val CLIENT_ID = "ded6895e"  //jkandcoding
    }
//@Headers("apikey: $CLIENT_ID")
    @GET("?apikey=$CLIENT_ID")
    suspend fun searchMoviesByTitle(
        @Query("s") title: String

    ): Response<MovieResponse>

    @GET("?apikey= $CLIENT_ID")
    suspend fun searchMoviesByImdbID(
        @Query("i") imdbID: String
    ): Response<Movie>

}