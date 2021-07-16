package com.jkandcoding.android.favorite.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jkandcoding.android.favorite.R
import com.jkandcoding.android.favorite.database.MovieDB
import com.jkandcoding.android.favorite.ui.home.MovieFavoriteAdapter.MovieFavoriteViewHolder

class MovieFavoriteAdapter(
    var favoriteMoviesList: List<MovieDB>,
    private val deleteListener: OnDeleteBtnClickListener
) : RecyclerView.Adapter<MovieFavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieFavoriteViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_movie, parent, false)
        return MovieFavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieFavoriteViewHolder, position: Int) {
        val currentFavMovie = favoriteMoviesList[position]
        holder.tvFavoriteTitle.text = currentFavMovie.Title
        holder.tvFavoriteYear.text = currentFavMovie.Year
        holder.iBtnFavoriteDelete.setOnClickListener {
            deleteListener.onDeleteClick(currentFavMovie)
        }
    }

    override fun getItemCount(): Int {
        return favoriteMoviesList.size
    }

    class MovieFavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFavoriteTitle: TextView = itemView.findViewById(R.id.tv_favorite_title)
        val tvFavoriteYear: TextView = itemView.findViewById(R.id.tv_favorite_year)
        val iBtnFavoriteDelete: ImageButton = itemView.findViewById(R.id.iBtn_favorite_deleteBtn)

    }

    interface OnDeleteBtnClickListener {
        fun onDeleteClick(movie: MovieDB)
    }
}