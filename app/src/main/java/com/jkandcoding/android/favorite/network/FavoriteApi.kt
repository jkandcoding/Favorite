package com.jkandcoding.android.favorite.network



import com.jkandcoding.android.favorite.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface FavoriteApi {

    companion object {
        const val BASE_URL = ""
        const val CLIENT_ID = BuildConfig.OMDB_ACCESS_KEY
    }

    @GET("")
    suspend fun searchMoviesByTitle(
        @Query("boundingBox", encoded = true) boundingBox: String
//    ): Response<BetshopResponse>
    )  //todo dodaj Type responsa
}