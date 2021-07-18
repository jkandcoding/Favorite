package com.jkandcoding.android.favorite.ui.home

import android.os.Bundle
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
import com.jkandcoding.android.favorite.database.Movie
import com.jkandcoding.android.favorite.databinding.FragmentHomeBinding
import com.jkandcoding.android.favorite.ui.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home),
    MovieFavoriteAdapter.OnDeleteBtnClickListener,
    MovieFavoriteAdapter.OnFavoriteItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val viewModel by activityViewModels<MovieViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!      // return nonnullable type

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        setHasOptionsMenu(true)

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
                // if no movies in database, set empty view
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
                return true
            }
        })
    }

    private fun goToSearchFragment(query: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment(query)
        findNavController().navigate(action)
    }

    override fun onDeleteClick(deleteMovie: Movie) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage(getString(R.string.do_you_want_to_delete_this_movie))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteMovie(deleteMovie)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }

    override fun onFavItemClick(movie: Movie) {
        val action =
            HomeFragmentDirections.actionHomeFragmentToDetailsFragment(movie.Title, movie)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}