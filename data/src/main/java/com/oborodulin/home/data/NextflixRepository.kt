package com.oborodulin.home.data

import com.oborodulin.home.data.remote.RemoteDataSource
import com.oborodulin.home.domain.entity.Movies
import com.oborodulin.home.domain.gateway.HomeGateway
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by tfakioglu on 13.December.2021
 */
class NextflixRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
): HomeGateway {

    override suspend fun getPopularMovies(page: Int): Response<Movies> {
        return remoteDataSource.getPopularMovies(page)
    }

    override suspend fun getNowPlayingMovies(page: Int): Response<Movies> {
        return remoteDataSource.getNowPlaying(page)
    }

    override suspend fun getUpcomingMovies(page: Int): Response<Movies> {
        return remoteDataSource.getUpcoming(page)
    }

}