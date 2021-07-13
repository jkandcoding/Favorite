package com.jkandcoding.android.favorite.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.jkandcoding.android.favorite.R
import com.jkandcoding.android.favorite.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val args by navArgs<DetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDetailsBinding.bind(view)

        binding.apply {
            val movie = args.movieDetails

            val genres = movie.Genre?.split(",")?.toTypedArray()
            if (genres != null) {
                for (genre in genres)  {
                    val chip = LayoutInflater.from(context).inflate(R.layout.chip_genre, null) as Chip
                    chip.text = genre
                    cgDetailsChipGroup.addView(chip)
                }
            }

            tvDetailsTitle.text = movie.Title
            tvDetailsReleased.text = movie.Released
            tvDetailsRuntime.text = movie.Runtime
            tvDetailsImdbRating.text = movie.imdbRating + "/10"
            tvDetailsPlot.text = movie.Plot
        }
    }
}