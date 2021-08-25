package com.jkandcoding.android.favorite.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jkandcoding.android.favorite.R
import com.jkandcoding.android.favorite.database.Movie
import com.jkandcoding.android.favorite.other.MovieHelperPojo

class MovieSearchAdapter(
    private val dataset: MutableList<RecyclerViewContainer>,
    private val listener: OnItemClickListener,
    private val saveMovieListener: OnSaveMovieBtnClickListener
) : RecyclerView.Adapter<MovieSearchAdapter.MovieViewHolder>() {

    // all movies from database -> used for marking movies from search result
    var favorites: List<Movie> = listOf()
    var favoritesImdbs: List<String> = listOf()

    fun setFavoriteList(fav: List<Movie>) {
        this.favorites = fav
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // holds all the child views
        private val viewMap: MutableMap<Int, View> = HashMap()

        init {
            findViewItems(itemView)
        }

        private fun findViewItems(itemView: View) {
            addToMap(itemView)
            if (itemView is ViewGroup) {
                val childCount = itemView.childCount
                (0 until childCount)
                    .map { itemView.getChildAt(it) }
                    .forEach { findViewItems(it) }
            }
        }

        private fun addToMap(itemView: View) {
            viewMap[itemView.id] = itemView
        }

        fun setHeader(@IdRes id: Int, year: String) {
            val view = (viewMap[id]
                ?: throw IllegalArgumentException("View for $id not found")) as? TextView
                ?: throw IllegalArgumentException("View for $id is not a TextView")
            view.text = year
        }

        fun setItems(
            item: MovieHelperPojo,
            @IdRes textViewId: Int,
            @IdRes imageViewId: Int,
            @IdRes toggleBtnId: Int,
        ) {
            val textView = (viewMap[textViewId]
                ?: throw IllegalArgumentException("View for $textViewId not found")) as? TextView
                ?: throw IllegalArgumentException("View for $textViewId is not a TextView")
            textView.text = item.Title

            val imageView = (viewMap[imageViewId]
                ?: throw IllegalArgumentException("View for $imageViewId not found")) as? ImageView
                ?: throw IllegalArgumentException("View for $imageViewId is not a ImageView")
            Glide.with(itemView)
                .load(item.Poster)
                .centerCrop()
                .into(imageView)

            val toggleBtnView = (viewMap[toggleBtnId]
                ?: throw IllegalArgumentException("View for $toggleBtnId not found")) as? ToggleButton
                ?: throw IllegalArgumentException("View for $toggleBtnId is not a TextView")

            toggleBtnView.setOnCheckedChangeListener(null)

            favoritesImdbs = favorites.map { movie -> movie.imdbID }
            item.isInDB = favoritesImdbs.contains(item.imdbID)
            // if movie is in favorites, check toggleBtnView
            toggleBtnView.isChecked = item.isInDB

            toggleBtnView.setOnCheckedChangeListener { _, _ ->
                if (item.isInDB) {
                    toggleBtnView.isChecked = false
                    item.isInDB = false
                    // movie will be deleted from db
                    saveMovieListener.onSaveMovieBtnClick(item, false)
                } else {
                    toggleBtnView.isChecked = true
                    item.isInDB = true
                    // movie will be saved to db
                    saveMovieListener.onSaveMovieBtnClick(item, true)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val inflatedView: View = when (viewType) {
            RowType.ROW.ordinal -> layoutInflater.inflate(R.layout.item_search_movie, parent, false)
            else -> layoutInflater.inflate(R.layout.item_header, parent, false)
        }
        return MovieViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val item = dataset[position]

        if (item.isHeader) {
            item.headerYear?.let {
                holder.setHeader(R.id.tv_header_year, item.headerYear!!)
            }
        } else {
            holder.setItems(
                item.movie!!,
                R.id.tv_search_title,
                R.id.iv_search_poster,
                R.id.tb_search_toggleBtn
            )
        }

        holder.itemView.setOnClickListener {
            if (!item.isHeader) {
                val pressedMovie = item.movie
                if (pressedMovie != null) {
                    listener.onItemClick(pressedMovie.imdbID)
                }
            }
        }
    }

    override fun getItemCount(): Int = dataset.size

    override fun getItemViewType(position: Int): Int {
        return if (dataset[position].isHeader) {
            0
        } else {
            1
        }
    }

    interface OnItemClickListener {
        fun onItemClick(imdbID: String)
    }

    interface OnSaveMovieBtnClickListener {
        fun onSaveMovieBtnClick(movieHelperPojo: MovieHelperPojo, saveInsteadDelete: Boolean)
    }
}