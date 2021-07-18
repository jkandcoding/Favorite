package com.jkandcoding.android.favorite.ui.details

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.jkandcoding.android.favorite.R
import com.jkandcoding.android.favorite.database.Movie
import com.jkandcoding.android.favorite.databinding.FragmentDetailsBinding
import com.jkandcoding.android.favorite.ui.MovieViewModel

class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val viewModel by activityViewModels<MovieViewModel>()
    private val args by navArgs<DetailsFragmentArgs>()
    private var movie: Movie? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        movie = args.movie

        val binding = FragmentDetailsBinding.bind(view)
        binding.apply {

            val genres = movie!!.Genre?.split(",")?.toTypedArray()
            if (genres != null) {
                for (genre in genres) {
                    val chip =
                        LayoutInflater.from(context).inflate(R.layout.chip_genre, null) as Chip
                    chip.text = genre.trim()
                    cgDetailsChipGroup.addView(chip)
                }
            }

            tvDetailsTitle.text = movie!!.Title
            tvDetailsReleased.text = movie!!.Released
            tvDetailsRuntime.text = movie!!.Runtime
            tvDetailsImdbRating.text = getString(R.string.from10, movie!!.imdbRating)
            tvDetailsPlot.text = movie!!.Plot
        }
        setHasOptionsMenu(true)
        viewModel.checkIfMovieIsInDb(movie!!.imdbID)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save -> {
            item.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_heart_on_white_24)
            // based
            viewModel.isMovieInDb.observe(viewLifecycleOwner) { it ->
                if (it == true) {
                    Snackbar.make(
                        this.requireView(),
                        getString(R.string.movie_is_already_in_favorites),
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    movie?.let { viewModel.insertMovie(it) }
                }
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}