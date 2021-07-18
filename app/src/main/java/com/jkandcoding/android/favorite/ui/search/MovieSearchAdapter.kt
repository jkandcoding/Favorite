package com.jkandcoding.android.favorite.ui.search

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.jkandcoding.android.favorite.R
import com.jkandcoding.android.favorite.database.MovieDB
import com.jkandcoding.android.favorite.database.Movie
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MovieSearchAdapter(
    private val context: Context,
    private val dataset: MutableList<RecyclerViewContainer>,
    private val listener: OnItemClickListener,
    private val saveMovieListener: OnSaveMovieBtnClickListener
) : RecyclerView.Adapter<MovieSearchAdapter.MovieViewHolder>() {

    var savedMovies: ArrayList<MovieDB> = arrayListOf()   // todo ovo za brisati
    var favorites: List<Movie> = listOf()

    fun setFavoriteList(fav: List<Movie>) {
        this.favorites = fav
        Log.d("jfjfh", "MovieSearchAdapter, favorites.size: " + favorites.size)
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
            item: MovieDB,
            @IdRes textViewId: Int,
            @IdRes toggleBtnId: Int,
        ) {
            //todo set onclicklistener on button inside item
            val textView = (viewMap[textViewId]
                ?: throw IllegalArgumentException("View for $textViewId not found")) as? TextView
                ?: throw IllegalArgumentException("View for $textViewId is not a TextView")
            textView.text = item.Title

            val toggleBtnView = (viewMap[toggleBtnId]
                ?: throw IllegalArgumentException("View for $toggleBtnId not found")) as? ToggleButton
                ?: throw IllegalArgumentException("View for $toggleBtnId is not a TextView")

            toggleBtnView.setOnCheckedChangeListener(null)

            // if movie is in favorites, check toggleBtnView
            if (favorites.isNotEmpty()) {
                Log.d("jfjfh", "MovieSearchAdapter, setItems - favorites.size: " + favorites.size)
                for (movie in favorites) {
                    Log.d("jfjfh", "MovieSearchAdapter, setItems - movie.imdbID: " + movie.imdbID)
                    Log.d("jfjfh", "MovieSearchAdapter, setItems - item.imdbID: " + item.imdbID)
                    if (movie.imdbID == item.imdbID) {
                        item.isFavorite = true
                        item.isInDB = true  //todo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
                        Log.d("jfjfh", "MovieSearchAdapter, setItems - item.imdbID: " + item.imdbID)
                    }
                }
            }

            toggleBtnView.isChecked = item.isFavorite

            toggleBtnView.setOnCheckedChangeListener { _, b ->

                if (item.isFavorite) {
                    if (item.isInDB) {

                        Toast.makeText(context, "Deleting movie from favorites", Toast.LENGTH_SHORT).show()

                    }
                    toggleBtnView.isChecked = false
                    item.isFavorite = false
                    item.isInDB = false
                    // delete movie
                    saveMovieListener.onSaveMovieBtnClick(item, false)
                    //  savedMovies.remove(item)
                } else {
                    toggleBtnView.isChecked = true
                    item.isFavorite = true
                    // save movie
                    saveMovieListener.onSaveMovieBtnClick(item, true)
                    //  savedMovies.add(item)
                }
                // saveMovieListener.onSaveMovieBtnClick(savedMovies)
                Log.d("favButton", "setOnCheckedChangeListener, savedMovies: " + savedMovies)
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
            holder.setItems(item.movie!!, R.id.tv_search_title, R.id.tb_search_toggleBtn)
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
        if (dataset[position].isHeader) {
            return 0
        } else {
            return 1
        }
    }

    interface OnItemClickListener {
        fun onItemClick(imdbID: String)
    }

    interface OnSaveMovieBtnClickListener {
        fun onSaveMovieBtnClick(movieDB: MovieDB, addToSaveList: Boolean)
    }
}