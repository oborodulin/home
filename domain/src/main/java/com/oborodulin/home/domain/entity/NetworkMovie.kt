package com.oborodulin.home.domain.entity

import com.oborodulin.home.domain.util.Constants

/**
 * Created by tfakioglu on 13.December.2021
 */
data class NetworkMovie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val voteAverage: Double?,
) {
    val posterUrl = Constants.POSTER_URL + posterPath
}