package com.jkandcoding.android.favorite.network

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.jkandcoding.android.favorite.database.MovieDB
import com.jkandcoding.android.favorite.database.MovieDao
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val favoriteApi: FavoriteApi,
    private val movieDao: MovieDao
) {
    suspend fun getMoviesByTitle(title: String): Response<MovieResponse> {
        val responseMovie: Response<MovieResponse> = favoriteApi.searchMoviesByTitle(title)
        Log.d("responseMovie", "Repository - response from api: " + responseMovie)
        return responseMovie
    }

    suspend fun getMovieByImdbID(imdbID: String): Response<Movie> {
        val responseMovie: Response<Movie> = favoriteApi.searchMoviesByImdbID(imdbID)
        Log.d("movieDetails", "Repository - response from api: " + responseMovie)
        return responseMovie
    }


   val favoriteMovies: LiveData<List<Movie>> = movieDao.selectAllFavorites()

    @WorkerThread
    suspend fun insert(movie: Movie) {
        movieDao.insertMoview(movie)
    }

    @WorkerThread
    suspend fun delete(movie: Movie) {
        movieDao.deleteMovie(movie)
    }

@WorkerThread
   suspend fun isMovieInDb(imdbID: String): Int = movieDao.isMovieInDb(imdbID)

}