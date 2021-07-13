package com.jkandcoding.android.favorite.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.jkandcoding.android.favorite.R
import com.jkandcoding.android.favorite.database.MovieDB
import com.jkandcoding.android.favorite.databinding.FragmentHomeBinding
import com.jkandcoding.android.favorite.network.Movie
import com.jkandcoding.android.favorite.other.Resource
import com.jkandcoding.android.favorite.other.Status
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.concurrent.schedule
import kotlin.collections.ArrayList

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), MovieSearchAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private var itemList = mutableListOf<RecyclerViewContainer>()
    private val headerYearsArray: ArrayList<String> = arrayListOf()

    private var searchMovieList: List<MovieDB> = listOf()
    private var searchMovieListOrdered: List<MovieDB> = listOf()

    private var searchMovieByImdbID: Movie? = null

    private val handler: Handler = Handler(Looper.getMainLooper())

    private val viewModel by viewModels<MovieViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!      // return nonnullable type



//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//
//        val viewOfLayout = inflater.inflate(R.layout.fragment_home, container, false)
//        return viewOfLayout
//
//    }

    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        setHasOptionsMenu(true)

        setData()


    }

    private fun setRecyclerView() {
        viewManager = LinearLayoutManager(this.context)
        val myAdapter = MovieSearchAdapter(itemList, this)
        myAdapter.notifyDataSetChanged()

        //binding.searchRecyclerView.apply {
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

                    resource.data?.let { movieResponse ->
                        if (movieResponse.Search.isNotEmpty()) {
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
                        }
                    }
                }
                Status.LOADING -> {
                    Log.d("responseMovie", "HomeFragment - LOADING...")
                }
                Status.ERROR -> {
                    Snackbar.make(
                        binding.root,
                        "Can't get movies " + resource.message,
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

        inflater.inflate(R.menu.menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
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

    override fun onResume() {
        super.onResume()
        setAdapterListWithMoviesAndHeaders()
        Log.d("movieDetails", "onResume " + itemList.size)
        setRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(imdbID: String) {
        Log.d("movieDetails", "HomeFragment-onItemClick, imdbID: " + imdbID)
        // get movieDetails from api and show them in DetailsFragment
        //viewModel.setImdbIDForSearch(imdbID)

        viewModel.getMovieDetails(imdbID)

        //todo this must be handled differently
        Handler(Looper.getMainLooper()).postDelayed({
            seeMovieResultFromApi()
        }, 500)

    }

    private fun seeMovieResultFromApi() {
        Log.d("movieDetails", "HomeFragment-seeMovieResultFromApi, resMovie: " + viewModel.resMovieDetails.value)
        viewModel.resMovieDetails.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    resource.data?.let { movieResponse ->

                        searchMovieByImdbID = movieResponse
                        Log.d(
                            "movieDetails",
                            "HomeFragment - SUCCESS - searchMovieDetails: " + searchMovieByImdbID!!.Title
                        )
                        searchMovieByImdbID?.let { goToDetailsFragment(it) }

                    }
                }
                Status.LOADING -> {
                    Log.d("movieDetails", "HomeFragment-searchMovieDetails - LOADING...")
                }
                Status.ERROR -> {
                    Snackbar.make(
                        binding.root,
                        "Can't get movie details, " + resource.message,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    Log.d("movieDetails", "HomeFragment - ERROR: " + resource.message)
                }
            }
        }
    }

    private fun goToDetailsFragment(movie: Movie) {
        viewModel.setImdbIDForSearch("")
        val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(movie)
        Log.d("movieDetails", "HomeFragment-goToDetailsFragment, title: " + movie.Title)
        findNavController().navigate(action)
    }
}