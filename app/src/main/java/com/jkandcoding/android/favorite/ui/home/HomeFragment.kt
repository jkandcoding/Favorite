package com.jkandcoding.android.favorite.ui.home

import android.os.Bundle
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
import com.jkandcoding.android.favorite.R
import com.jkandcoding.android.favorite.database.MovieDB
import com.jkandcoding.android.favorite.databinding.FragmentHomeBinding
import com.jkandcoding.android.favorite.ui.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val viewModel by activityViewModels<MovieViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!      // return nonnullable type

//    companion object {
//        fun newInstance(): HomeFragment = HomeFragment()
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        setHasOptionsMenu(true)

        getFavoriteMoviesFromDb()


    }

    private fun getFavoriteMoviesFromDb() {
        viewModel.favMovies.observe(viewLifecycleOwner) {
            setRecyclerView(it)
        }
    }

    private fun setRecyclerView(favMovies: List<MovieDB>) {
        viewManager = LinearLayoutManager(this.context)
        val myAdapter = MovieFavoriteAdapter(favMovies)
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
                    goToSearchFragment()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("querySearch", "HOME - onQueryTextChange ")
                return true
            }
        })
    }

    private fun goToSearchFragment() {
        val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}