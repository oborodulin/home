package com.oborodulin.home.domain.gateway

import com.oborodulin.home.domain.model.Movies
import retrofit2.Response

/**
 * Created by tfakioglu on 13.December.2021
 */
interface HomeGateway {
    suspend fun getPopularMovies(page: Int): Response<Movies>
    suspend fun getNowPlayingMovies(page: Int): Response<Movies>
    suspend fun getUpcomingMovies(page: Int): Response<Movies>
}