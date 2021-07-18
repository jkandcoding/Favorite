package com.jkandcoding.android.favorite.network

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
        return favoriteApi.searchMoviesByTitle(title)
    }

    suspend fun getMovieByImdbID(imdbID: String): Response<Movie> {
        return favoriteApi.searchMoviesByImdbID(imdbID)
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