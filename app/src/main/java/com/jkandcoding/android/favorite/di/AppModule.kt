package com.jkandcoding.android.favorite.di

import android.content.Context
import android.net.ConnectivityManager
import com.jkandcoding.android.favorite.database.MovieDao
import com.jkandcoding.android.favorite.database.MovieDatabase
import com.jkandcoding.android.favorite.network.ConnectivityCheckingInterceptor
import com.jkandcoding.android.favorite.network.FavoriteApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(connectivityManager: ConnectivityManager): Retrofit =
        Retrofit.Builder()
            .baseUrl(FavoriteApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(ConnectivityCheckingInterceptor(connectivityManager))
                    .build()
            )
            .build()

    @Provides
    @Singleton
    fun provideFavoriteApi(retrofit: Retrofit): FavoriteApi =
        retrofit.create(FavoriteApi::class.java)

    @Provides
    fun provideMovieDao(movieDatabase: MovieDatabase): MovieDao = movieDatabase.movieDao()


    @Provides
    @Singleton
    fun provideMovieDatabase(
        @ApplicationContext context: Context
    ): MovieDatabase = MovieDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideConnectivityManager(
        @ApplicationContext context: Context
    ) = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

}