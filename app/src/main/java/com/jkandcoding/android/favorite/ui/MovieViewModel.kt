package com.jkandcoding.android.favorite.ui

import android.util.Log
import androidx.lifecycle.*
import com.jkandcoding.android.favorite.network.Movie
import com.jkandcoding.android.favorite.database.MovieDB
import com.jkandcoding.android.favorite.network.MovieRepository
import com.jkandcoding.android.favorite.network.MovieResponse
import com.jkandcoding.android.favorite.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private var titleToSearch: String = ""
    private var imdbIDToSearch: String = ""

    private val _res = MutableLiveData<Resource<MovieResponse>>()
    val res: LiveData<Resource<MovieResponse>>
        get() = _res

    private var _resMovieDetails = MutableLiveData<Resource<Movie>>()
    val resMovieDetails: LiveData<Resource<Movie>>
        get() = _resMovieDetails

//    private var _favoriteMovies = MutableLiveData<List<MovieDB>>()
//    val favoriteMovies: LiveData<List<MovieDB>>
//        get() = _favoriteMovies

    private fun getMovies(title: String) = viewModelScope.launch {
        try {
            _res.postValue(Resource.loading(null))
            repository.getMoviesByTitle(title).let {
                if (it.isSuccessful) {
                    _res.postValue(Resource.success(it.body()))
                } else {
                    _res.postValue(Resource.error(it.raw().message(), null))
                }
            }
        } catch (e: IOException) {
            _res.postValue(Resource.error(e.localizedMessage, null))
            e.printStackTrace()
        }
    }

    fun getMovieDetails(imdbId: String) = viewModelScope.launch {
        Log.d("movieDetails", "VIEWMODEL, resMovie: " + resMovieDetails.value)
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
            _resMovieDetails.postValue(Resource.error(e.localizedMessage, null))
            e.printStackTrace()
        }

    }

    fun setQueryForSearch(query: String) {
        titleToSearch = query
        getMovies(titleToSearch)
    }

    fun setImdbIDForSearch(query: String) {
        imdbIDToSearch = query
        getMovieDetails(imdbIDToSearch)
    }

//-----------DATABASE---------

    // insert favorite movie to room database
    fun insertMovie(movie: MovieDB) = viewModelScope.launch {
        repository.insert(movie)
    }

    // delete favorite movie from room database
    fun deleteMovie(movie: MovieDB) = viewModelScope.launch {
        repository.delete(movie)
    }




    val favMovies: LiveData<List<MovieDB>> = repository.favoriteMovies

}