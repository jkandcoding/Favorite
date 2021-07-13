package com.jkandcoding.android.favorite.ui

import com.jkandcoding.android.favorite.database.MovieDB

class RecyclerViewContainer(
    var movie: MovieDB?,
    var isHeader: Boolean,
    var headerYear: String?
) {
}