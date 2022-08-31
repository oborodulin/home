package com.oborodulin.home.data.remote

import com.oborodulin.home.domain.model.Movies
import com.oborodulin.home.domain.model.NetworkMovie
import com.oborodulin.home.data.remote.response.Movie
import com.oborodulin.home.data.remote.response.MoviesResponse
import retrofit2.Response

/**
 * Created by tfakioglu on 14.December.2021
 */
fun Response<MoviesResponse>.asMovies(): Response<Movies> = Response.success(body()?.let {
    Movies(
        results = it.results.map { movies -> movies.asMovie() },
        currentPage = 1,
        totalPages = it.totalPages,
    )
})

fun Movie.asMovie() = NetworkMovie(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    voteAverage = voteAverage,
)