package com.jkandcoding.android.favorite.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jkandcoding.android.favorite.database.Movie
import com.jkandcoding.android.favorite.network.ConnectivityCheckingInterceptor
import com.jkandcoding.android.favorite.network.MovieRepository
import com.jkandcoding.android.favorite.network.MovieResponse
import com.jkandcoding.android.favorite.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    //-------------- OMDB API ----------------------
    private var titleToSearch: String = ""

    // result from http://www.omdbapi.com/?apikey=[apikey]&s=[movieTitle]
    private val _res = MutableLiveData<Resource<MovieResponse>>()
    val res: LiveData<Resource<MovieResponse>>
        get() = _res

    // result from http://www.omdbapi.com/?apikey=[apikey]&i=[imbdID]
    private var _resMovieDetails = MutableLiveData<Resource<Movie>>()
    val resMovieDetails: LiveData<Resource<Movie>>
        get() = _resMovieDetails

    // get list of movies from omdbApi searched with title
    private fun getMovies(title: String) = viewModelScope.launch {
        try {
            _res.postValue(Resource.loading(null))
            repository.getMoviesByTitle(title).let {
                if (it.isSuccessful) {
                    if (it.body()?.Response == "True") {
                        _res.postValue(Resource.success(it.body()))
                    } else {
                        _res.postValue(it.body()?.Error?.let { errorMsg ->
                            Resource.error(errorMsg, null)
                        })
                    }
                } else {
                    _res.postValue(Resource.error(it.raw().message(), null))
                }
            }
        } catch (e: ConnectivityCheckingInterceptor.NoInternetException) {
            _res.postValue(Resource.error(e.message, null))
        } catch (e: UnknownHostException) {
            _res.postValue(e.message?.let {
                Resource.error(it, null)
            })
        }
    }

    // get details of particular movie from omdbApi searched with imdbID
    fun getMovieDetails(imdbId: String) = viewModelScope.launch {
        try {
            _resMovieDetails.postValue(Resource.loading(null))
            repository.getMovieByImdbID(imdbId).let {
                if (it.isSuccessful) {
                    _resMovieDetails.postValue(Resource.success(it.body()))
                } else {
                    _resMovieDetails.postValue(Resource.error(it.raw().message(), null))
                }
            }
        } catch (e: IOException) {
            _resMovieDetails.postValue(e.message?.let { Resource.error(it, null) })
            e.printStackTrace()
        }
    }

    // set title for omdbApi when searching movies
    fun setQueryForSearch(query: String) {
        titleToSearch = query
        getMovies(titleToSearch)
    }

//-----------DATABASE---------

    private val _isMovieInDb = MutableLiveData<Boolean>()
    val isMovieInDb: LiveData<Boolean>
        get() = _isMovieInDb

    // check if movie is already in database - used in DetailsFragment
    fun checkIfMovieIsInDb(imdbID: String) = viewModelScope.launch {
        repository.isMovieInDb(imdbID)
        if (repository.isMovieInDb(imdbID) > 0) {
            _isMovieInDb.postValue(true)
        } else {
            _isMovieInDb.postValue(false)
        }
    }

    // insert favorite movie in database
    fun insertMovie(movie: Movie) = viewModelScope.launch {
        repository.insert(movie)
    }

    // delete favorite movie from database
    fun deleteMovie(movie: Movie) = viewModelScope.launch {
        repository.delete(movie)
    }

    // delete movie with imdbID - called from SearchFragment
    fun deleteMovieWithImdbID(imdbID: String) = viewModelScope.launch {
        repository.deleteMovieWithImdbID(imdbID)
    }

    // get all movies ("Favorites") from database and show them on HomeFragment
    val favMovies: LiveData<List<Movie>> = repository.favoriteMovies

}