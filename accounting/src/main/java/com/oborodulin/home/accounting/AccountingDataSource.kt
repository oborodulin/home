package com.oborodulin.home.accounting

import androidx.paging.PagingSource
import androidx.paging.PagingState
//import com.oborodulin.home.domain.entity.NetworkMovie
import com.oborodulin.home.domain.usecase.AccountingUseCase
import java.lang.IllegalStateException
import javax.inject.Inject

/**
 * Created by tfakioglu on 12.December.2021
 */
class AccountingDataSource @Inject constructor(/*private val nowPlayingUseCase: NowPlayingUseCase*/)
//    :    PagingSource<Int, NetworkMovie>()
{

/*    override fun getRefreshKey(state: PagingState<Int, NetworkMovie>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NetworkMovie> {
        try {
            val nextPage = params.key ?: 1
            val movieResponse = nowPlayingUseCase(nextPage)

            if (movieResponse.body()?.results.isNullOrEmpty()){
                return LoadResult.Error(throw Exception("Something went wrong"))
            }

            val list = movieResponse.body()?.results ?: emptyList()

            return LoadResult.Page(
                data = list,
                prevKey =
                if (nextPage == 1) null
                else nextPage - 1,
                nextKey = nextPage.plus(1)
            )
        } catch (t: Throwable) {
            return LoadResult.Error(t)
        }
    }

 */
}
