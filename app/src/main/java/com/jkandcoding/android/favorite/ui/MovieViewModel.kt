package com.jkandcoding.android.favorite.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jkandcoding.android.favorite.database.Movie
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
) : ViewModel()  {

    private var titleToSearch: String = ""

    private val _res = MutableLiveData<Resource<MovieResponse>>()

    val res: LiveData<Resource<MovieResponse>>
        get() = _res

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

    fun setQueryForSearch(query: String) {
        titleToSearch = query
        getMovies(titleToSearch)
    }

}