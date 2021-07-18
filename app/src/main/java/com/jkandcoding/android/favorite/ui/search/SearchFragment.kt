package com.jkandcoding.android.favorite.ui.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.jkandcoding.android.favorite.R
import com.jkandcoding.android.favorite.database.MovieDB
import com.jkandcoding.android.favorite.databinding.FragmentSearchBinding
import com.jkandcoding.android.favorite.database.Movie
import com.jkandcoding.android.favorite.other.Status
import com.jkandcoding.android.favorite.ui.MovieViewModel
import com.jkandcoding.android.favorite.ui.home.HomeFragment

class SearchFragment : Fragment(R.layout.fragment_search), MovieSearchAdapter.OnItemClickListener,
    MovieSearchAdapter.OnSaveMovieBtnClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var itemList = mutableListOf<RecyclerViewContainer>()
    private val headerYearsArray: ArrayList<String> = arrayListOf()

    private var searchMovieList: List<MovieDB> = listOf()
    private var searchMovieListOrdered: List<MovieDB> = listOf()

    private var searchMovieByImdbID: Movie? = null

    private var movieListForSave: List<MovieDB> = listOf()

    private var favMovies: List<Movie> = listOf()

    private val viewModel by activityViewModels<MovieViewModel>()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
        private const val SAVE = "save"
        private const val SHOW = "show"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        setHasOptionsMenu(true)

        setFavoritesInsideMovieSearchAdapter()
        setData()

    }

    private fun setFavoritesInsideMovieSearchAdapter() {
        viewModel.favMovies.observe(viewLifecycleOwner) {
           favMovies = it
        }
    }

    private fun setRecyclerView() {
        viewManager = LinearLayoutManager(this.context)
        val myAdapter = MovieSearchAdapter(requireContext(), itemList, this, this)
        myAdapter.setFavoriteList(favMovies)
        myAdapter.notifyDataSetChanged()

        recyclerView = binding.searchRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = myAdapter
            visibility = View.VISIBLE
        }
    }

    private fun setData() {
        viewModel.res.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    binding.pbSearchProgressBar.visibility = View.GONE
                    resource.data?.let { movieResponse ->
                        if (movieResponse.Search?.isNotEmpty() == true) {
                            searchMovieList = movieResponse.Search
                            Log.d(
                                "responseMovie",
                                "HomeFragment - SUCCESS - searchMovieList.size: " + searchMovieList.size
                            )
                            Log.d(
                                "responseMovie",
                                "HomeFragment - SUCCESS searchMovieList: " + searchMovieList
                            )
                            searchMovieListOrdered =
                                searchMovieList.sortedWith(compareBy { it.Year })

                            setAdapterListWithMoviesAndHeaders()
                            setRecyclerView()

                            // empty list from API
                        } else {
                            Log.d("responseMovie", "HomeFragment - SUCCESS but empty list")
                            Snackbar.make(
                                binding.root,
                                "Empty response, no data here " + resource.message,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                Status.LOADING -> {
                    Log.d("responseMovie", "HomeFragment - LOADING...")
                    binding.pbSearchProgressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    binding.pbSearchProgressBar.visibility = View.GONE
                    Snackbar.make(
                        binding.root,
                        "Can't get to movies. " + (resource.message),
                        Snackbar.LENGTH_SHORT
                    ).show()


                    Log.d("responseMovie", "HomeFragment - ERROR: " + resource.message)
                }
            }
        }

    }

    private fun setAdapterListWithMoviesAndHeaders() {
        itemList.clear()
        headerYearsArray.clear()
        for (i in (0 until searchMovieListOrdered.size)) {
            if (!headerYearsArray.contains(searchMovieListOrdered[i].Year)) {
                itemList.add(
                    RecyclerViewContainer(
                        null,
                        true,
                        searchMovieListOrdered[i].Year
                    )
                )
            }
            itemList.add(
                RecyclerViewContainer(
                    searchMovieListOrdered[i],
                    false,
                    null
                )
            )
            headerYearsArray.add(searchMovieListOrdered[i].Year)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.action_search2)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("querySearch", "onQueryTextSubmit ")
                if (query != null) {
                    Log.d("querySearch", "onQueryTextSubmit, query != null ")
                    viewModel.setQueryForSearch(query)
                    searchView.clearFocus() // it removes keyboard
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("querySearch", "onQueryTextChange ")

                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setAdapterListWithMoviesAndHeaders()
        //  Log.d("movieDetails", "SearchFragment - onResume " + itemList.size)
        setRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(imdbID: String) {
        getAllMovieDataFromApi(imdbID, SHOW)

        // todo ovo nize je null
//        searchMovieByImdbID?.let { goToDetailsFragment(it) }
//        searchMovieByImdbID = null
    }

    private fun getAllMovieDataFromApi(imdbID: String, saveOrShow: String) {
        Log.d("movieDetails", "SearchFragment-onItemClick, imdbID: " + imdbID)
        // get movieDetails from api and show them in DetailsFragment
        viewModel.getMovieDetails(imdbID)

        //todo this must be handled differently
        //this sets searchMovieByImdbID variable
        Handler(Looper.getMainLooper()).postDelayed({
            seeMovieResultFromApi(saveOrShow)
        }, 500)
    }

    private fun seeMovieResultFromApi(saveOrShow: String) {
        Log.d(
            "movieDetails",
            "SearchFragment-seeMovieResultFromApi, resMovie: " + viewModel.resMovieDetails.value
        )
        viewModel.resMovieDetails.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    binding.pbSearchProgressBar.visibility = View.GONE
                    resource.data?.let { movieResponse ->
                        searchMovieByImdbID = movieResponse
                        Log.d(
                            "movieDetails",
                            "SearchFragment - SUCCESS - searchMovieDetails: " + searchMovieByImdbID!!.Title
                        )
                        if (saveOrShow == SAVE) {
                            viewModel.insertMovie(searchMovieByImdbID!!)
                        } else {
                            goToDetailsFragment(searchMovieByImdbID!!)
                        }
                        Log.d(
                            "movieDetails",
                            "SearchFragment-SHOW/SEND!!! - searchMovieByImdbID.title: " + searchMovieByImdbID!!.Title
                        )
                    }
                }
                Status.LOADING -> {
                    Log.d("movieDetails", "SearchFragment-searchMovieDetails - LOADING...")
                    if (saveOrShow == SHOW) {
                        binding.pbSearchProgressBar.visibility = View.VISIBLE
                    } else {
                        binding.pbSearchProgressBar.visibility = View.GONE
                    }

                }
                Status.ERROR -> {
                    binding.pbSearchProgressBar.visibility = View.GONE
                    Snackbar.make(
                        binding.root,
                        "Can't get movie details, " + resource.message,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    Log.d("movieDetails", "SearchFragment - ERROR: " + resource.message)
                }
            }
        }
    }

    private fun goToDetailsFragment(movie: Movie) {
        Log.d("movieDetails", "SearchFragment - goToDetailsFragment: " + movie.Title)
        val action =
            SearchFragmentDirections.actionSearchFragmentToDetailsFragment(movie, movie.Title)
        findNavController().navigate(action)
    }

    override fun onSaveMovieBtnClick(movieDB: MovieDB, addToSaveList: Boolean) {
        // movieListForSave = movie
        if (addToSaveList == true) {
            // viewModel.searchMoviesForSave?.add(movie)

            // search for movieDetails for every movie inside movieListForSave and save to DB
            getAllMovieDataFromApi(movieDB.imdbID, SAVE)

        } else {
//            val helperList: List<Movie>? = viewModel.favMoviesHelper
//            if (helperList != null) {
//                Log.d(
//                    "movieDetails",
//                    "SearchFragment -------- onSaveMovieBtnClick: " + helperList.size
//                )
//            }
//            if (helperList != null) {
//                for (movie in helperList) {
//                    if (movie.imdbID != movieDB.imdbID) {
//                        viewModel.deleteMovieWithImdbID(movieDB.imdbID)
//                    }
//                }
//            } else {
//                viewModel.deleteMovieWithImdbID(movieDB.imdbID)
//            }
            viewModel.deleteMovieWithImdbID(movieDB.imdbID)
        }

        Log.d(
            "favButton",
            "SearchFragment - onSaveMovieBtnClick, movieListForSave.size: " + movieListForSave.size
        )

    }


}