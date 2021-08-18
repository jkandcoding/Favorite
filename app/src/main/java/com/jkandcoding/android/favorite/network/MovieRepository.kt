package com.jkandcoding.android.favorite.network

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.jkandcoding.android.favorite.database.Movie
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
        var movie1 = favoriteApi.searchMoviesByTitle(title)
        Log.d("jhjhj movie1", movie1.toString())
        return movie1
    }

    suspend fun getMovieByImdbID(imdbID: String): Response<Movie> {
        var movie = favoriteApi.searchMoviesByImdbID(imdbID)
        Log.d("jhjhj", movie.toString())
        return movie
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
    suspend fun deleteMovieWithImdbID(imdbID: String) {
        movieDao.deleteMovieWithIMDBID(imdbID)
    }

    @WorkerThread
    suspend fun isMovieInDb(imdbID: String): Int = movieDao.isMovieInDb(imdbID)

}