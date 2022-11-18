package com.oborodulin.home.domain.old

//import com.oborodulin.home.data.local.db.entities.Movies
import com.oborodulin.home.domain.gateway.HomeGateway
import javax.inject.Inject

/**
 * Created by tfakioglu on 14.December.2021
 */
class Accounting1UseCase @Inject constructor(
    private val homeGateway: HomeGateway,
) {

/*
    suspend operator fun invoke(page: Int): Response<Movies> {

        return homeGateway.getNowPlayingMovies(page)
    }

 */
}