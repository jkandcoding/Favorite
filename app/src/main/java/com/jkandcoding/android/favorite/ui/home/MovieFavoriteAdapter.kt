package com.jkandcoding.android.favorite.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jkandcoding.android.favorite.R
import com.jkandcoding.android.favorite.database.Movie
import com.jkandcoding.android.favorite.databinding.ItemFavoriteMovieBinding
import com.jkandcoding.android.favorite.ui.home.MovieFavoriteAdapter.MovieFavoriteViewHolder

class MovieFavoriteAdapter(
    private var favoriteMoviesList: List<Movie>,
    private val deleteListener: OnDeleteBtnClickListener,
    private val itemClickListener: OnFavoriteItemClickListener
) : RecyclerView.Adapter<MovieFavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieFavoriteViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_movie, parent, false)
        return MovieFavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieFavoriteViewHolder, position: Int) {
        val currentFavMovie = favoriteMoviesList[position]
        with(holder) {
            binding.apply {
                tvFavoriteTitle.text = currentFavMovie.Title
                tvFavoriteYear.text = currentFavMovie.Year
                if (currentFavMovie.Poster !== null) {
                    Glide.with(itemView)
                        .load(currentFavMovie.Poster)
                        .centerCrop()
                        .into(ivFavoritePoster)
                }

                iBtnFavoriteFavBtn.setOnClickListener {
                    deleteListener.onDeleteClick(currentFavMovie)
                }
            }
        }
        holder.itemView.setOnClickListener {
            itemClickListener.onFavItemClick(currentFavMovie)
        }
    }

    override fun getItemCount(): Int {
        return favoriteMoviesList.size
    }

    class MovieFavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemFavoriteMovieBinding.bind(itemView)
    }

    interface OnFavoriteItemClickListener {
        fun onFavItemClick(movie: Movie)
    }

    interface OnDeleteBtnClickListener {
        fun onDeleteClick(deleteMovie: Movie)
    }
}