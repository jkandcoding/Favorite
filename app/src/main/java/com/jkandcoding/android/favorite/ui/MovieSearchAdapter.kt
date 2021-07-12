package com.jkandcoding.android.favorite.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.jkandcoding.android.favorite.R
import com.jkandcoding.android.favorite.database.Movie

class MovieSearchAdapter(private val dataset: MutableList<RecyclerViewContainer>) : RecyclerView.Adapter<MovieSearchAdapter.MovieViewHolder>() {

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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
            val view = (viewMap[id] ?:
            throw IllegalArgumentException("View for $id not found")) as? TextView ?:
            throw IllegalArgumentException("View for $id is not a TextView")
            view.text = year
        }

        fun setItems(item: Movie, @IdRes textViewId: Int, @IdRes toggleBtnId: Int) {
            //todo set onclicklistener here to go to details fragment
            val view = (viewMap[textViewId] ?:
            throw IllegalArgumentException("View for $textViewId not found")) as? TextView ?:
            throw IllegalArgumentException("View for $textViewId is not a TextView")
            view.text = item.Title

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val inflatedView : View = when (viewType) {
            RowType.ROW.ordinal -> layoutInflater.inflate(R.layout.item_search_movie, parent, false)
            else -> layoutInflater.inflate(R.layout.item_header, parent, false)
        }
        return MovieViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
       val item = dataset[position]

        if (item.isHeader) {
            item.headerYear?.let {
                holder.setHeader(R.id.tv_header_year, item.headerYear!!) }
        } else {
            holder.setItems(item.movie!!, R.id.tv_search_title, R.id.tb_search_toggleBtn)
        }

        holder.itemView.setOnClickListener {
            println("Pressed at row $position")
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
}