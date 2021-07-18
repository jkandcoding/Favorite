package com.jkandcoding.android.favorite.ui.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.jkandcoding.android.favorite.database.Movie
import com.jkandcoding.android.favorite.databinding.FragmentSearchBinding
import com.jkandcoding.android.favorite.other.MovieHelperPojo
import com.jkandcoding.android.favorite.other.Status
import com.jkandcoding.android.favorite.ui.MovieViewModel

class SearchFragment : Fragment(R.layout.fragment_search), MovieSearchAdapter.OnItemClickListener,
    MovieSearchAdapter.OnSaveMovieBtnClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var itemList = mutableListOf<RecyclerViewContainer>()
    private val headerYearsArray: ArrayList<String> = arrayListOf()

    private var searchMovieList: List<MovieHelperPojo> = listOf()
    private var searchMovieListOrdered: List<MovieHelperPojo> = listOf()

    private var searchMovieByImdbID: Movie? = null

    private val viewModel by activityViewModels<MovieViewModel>()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val SAVE = "save"
        private const val SHOW = "show"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        setHasOptionsMenu(true)

        // set search results
        setData()
    }

    private fun setData() {
        viewModel.res.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    binding.pbSearchProgressBar.visibility = View.GONE
                    resource.data?.let { movieResponse ->
                        if (movieResponse.Search?.isNotEmpty() == true) {
                            searchMovieList = movieResponse.Search
                            searchMovieListOrdered =
                                searchMovieList.sortedWith(compareBy { it.Year })

                            setAdapterListWithMoviesAndHeaders()
                            setRecyclerView()

                            // empty list from API
                        } else {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.empty_response) + resource.message,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                Status.LOADING -> {
                    binding.pbSearchProgressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    binding.pbSearchProgressBar.visibility = View.GONE
                    Snackbar.make(
                        binding.root,
                        getString(R.string.cant_get_to_movies) + (resource.message),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setRecyclerView() {
        viewManager = LinearLayoutManager(this.context)
        val myAdapter = MovieSearchAdapter(requireContext(), itemList, this, this)
        viewModel.favMovies.observe(viewLifecycleOwner) {
            myAdapter.setFavoriteList(it)
            myAdapter.notifyDataSetChanged()
        }

        recyclerView = binding.searchRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = myAdapter
            visibility = View.VISIBLE
        }
    }

    private fun setAdapterListWithMoviesAndHeaders() {
        itemList.clear()
        headerYearsArray.clear()
        for (i in (searchMovieListOrdered.indices)) {
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
                if (query != null) {
                    viewModel.setQueryForSearch(query)
                    searchView.clearFocus() // it removes keyboard
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    override fun onItemClick(imdbID: String) {
        getAllMovieDataFromApi(imdbID, SHOW)
    }

    private fun getAllMovieDataFromApi(imdbID: String, saveOrShow: String) {
        // get movieDetails from api and show them in DetailsFragment
        viewModel.getMovieDetails(imdbID)

        //todo this must be handled differently
        //this sets searchMovieByImdbID variable
        Handler(Looper.getMainLooper()).postDelayed({
            seeMovieResultFromApi(saveOrShow)
        }, 500)
    }

    private fun seeMovieResultFromApi(saveOrShow: String) {
        viewModel.resMovieDetails.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    binding.pbSearchProgressBar.visibility = View.GONE
                    resource.data?.let { movieResponse ->
                        searchMovieByImdbID = movieResponse
                        if (saveOrShow == SAVE) {
                            viewModel.insertMovie(searchMovieByImdbID!!)
                        } else {
                            goToDetailsFragment(searchMovieByImdbID!!)
                        }
                    }
                }
                Status.LOADING -> {
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
                        getString(R.string.cant_get_movie_details) + resource.message,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun goToDetailsFragment(movie: Movie) {
        val action =
            SearchFragmentDirections.actionSearchFragmentToDetailsFragment(movie.Title, movie)
        findNavController().navigate(action)
    }

    override fun onSaveMovieBtnClick(movieHelperPojo: MovieHelperPojo, saveInsteadDelete: Boolean) {
        if (saveInsteadDelete) {
            // search for movieDetails and save to DB
            getAllMovieDataFromApi(movieHelperPojo.imdbID, SAVE)
        } else {
            viewModel.deleteMovieWithImdbID(movieHelperPojo.imdbID)
        }
    }

    override fun onResume() {
        super.onResume()
        setAdapterListWithMoviesAndHeaders()
        setRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}