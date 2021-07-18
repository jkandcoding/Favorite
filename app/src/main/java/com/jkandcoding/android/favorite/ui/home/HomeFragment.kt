package com.jkandcoding.android.favorite.ui.home

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jkandcoding.android.favorite.R
import com.jkandcoding.android.favorite.databinding.FragmentHomeBinding
import com.jkandcoding.android.favorite.database.Movie
import com.jkandcoding.android.favorite.ui.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), MovieFavoriteAdapter.OnDeleteBtnClickListener, MovieFavoriteAdapter.OnFavoriteItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val viewModel by activityViewModels<MovieViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!      // return nonnullable type

    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        setHasOptionsMenu(true)

     //   addSearchMoviesToDatabase()
        getFavoriteMoviesFromDb()
    }

    private fun getFavoriteMoviesFromDb() {
        viewModel.favMovies.observe(viewLifecycleOwner) { moviesFromDB ->
            if (moviesFromDB.isNotEmpty()) {
                setRecyclerView(moviesFromDB)
                binding.apply {
                    tvEmptyTitle.visibility = View.GONE
                    tvEmptyDescription.visibility = View.GONE
                    ivEmptyImage.visibility = View.GONE
                }
            } else {
                binding.apply {
                    homeRecyclerView.visibility = View.GONE
                    tvEmptyTitle.visibility = View.VISIBLE
                    tvEmptyDescription.visibility = View.VISIBLE
                    ivEmptyImage.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setRecyclerView(favMovies: List<Movie>) {
        viewManager = LinearLayoutManager(this.context)
        val myAdapter = MovieFavoriteAdapter(favMovies, this, this)
        myAdapter.notifyDataSetChanged()

            recyclerView = binding.homeRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = myAdapter
            visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_home, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    viewModel.setQueryForSearch(query)
                    searchView.clearFocus() // it removes keyboard
                    goToSearchFragment(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("querySearch", "HOME - onQueryTextChange ")
                return true
            }
        })
    }

    private fun goToSearchFragment(query: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment(query)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDeleteClick(deleteMovie: Movie) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Do you want to delete this movie?")
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteMovie(deleteMovie)
            }
            // negative button text and action
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()

    }

    override fun onFavItemClick(movie: Movie) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(movie, movie.Title)
        findNavController().navigate(action)
    }

//    private fun addSearchMoviesToDatabase() {
//        val moviesForSave: ArrayList<MovieDB>? = viewModel.searchMoviesForSave
//
//        if (moviesForSave != null) {
//           for (movie in moviesForSave) {
//               getAllMovieDataFromApi(movie.imdbID)
//           }
//        }
//        viewModel.searchMoviesForSave?.clear()
//    }

//    private fun getAllMovieDataFromApi(imdbID: String) {
//        Log.d("movieDetails", "HOMEFragment-onItemClick, imdbID: " + imdbID)
//        // get movieDetails from api and show them in DetailsFragment
//        viewModel.getMovieDetails(imdbID)
//
//        //todo this must be handled differently
//        //this sets searchMovieByImdbID variable
//        Handler(Looper.getMainLooper()).postDelayed({
//            seeMovieResultFromApi()
//        }, 500)
//    }

//    private fun seeMovieResultFromApi() {
//        Log.d(
//            "movieDetails",
//            "SearchFragment-seeMovieResultFromApi, resMovie: " + viewModel.resMovieDetails.value
//        )
//        viewModel.resMovieDetails.observe(viewLifecycleOwner) { resource ->
//            when (resource.status) {
//                Status.SUCCESS -> {
//                    binding.pbHomeProgressBar.visibility = View.GONE
//                    resource.data?.let {
//                        Log.d(
//                            "movieDetails",
//                            "SearchFragment - SUCCESS - searchMovieDetails: " + it.Title
//                        )
//
//                            viewModel.insertMovie(it)
//
//                        Log.d("movieDetails", "SearchFragment-SHOW/SEND!!! - searchMovieByImdbID.title: " + it.Title)
//                    }
//                }
//                Status.LOADING -> {
//                    Log.d("movieDetails", "SearchFragment-searchMovieDetails - LOADING...")
//                    binding.pbHomeProgressBar.visibility = View.VISIBLE
//                }
//                Status.ERROR -> {
//                    binding.pbHomeProgressBar.visibility = View.GONE
//                    Snackbar.make(
//                        binding.root,
//                        "Can't get movie details, " + resource.message,
//                        Snackbar.LENGTH_SHORT
//                    ).show()
//                    Log.d("movieDetails", "SearchFragment - ERROR: " + resource.message)
//                }
//            }
//        }
//    }

}