package com.oborodulin.home.data.local.db.repositories

//import com.oborodulin.home.domain.model.NetworkMovie
import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.mappers.PayerEntityMapper
import com.oborodulin.home.domain.model.Payer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

/**
 * Created by tfakioglu on 12.December.2021
 */
class PayerDataSourceImpl @Inject constructor(
    private val payerDao: PayerDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val payerEntityMapper: PayerEntityMapper
/*private val nowPlayingUseCase: NowPlayingUseCase*/
) : PayerDataSource
//    :    PagingSource<Int, NetworkMovie>()
{
    override fun getPayers() = payerDao.findAllDistinctUntilChanged()
        .map { list ->
            list.map {
                payerEntityMapper.toPayer(it)
            }
        }

    override fun getPayer(payerId: UUID) =
        payerDao.findByIdDistinctUntilChanged(payerId).map { payerEntityMapper.toPayer(it) }

    override suspend fun savePayer(payer: Payer) = withContext(dispatcher) {
        payerDao.insert(payerEntityMapper.toPayerEntity(payer))
    }

    override suspend fun deletePayer(payer: Payer) = withContext(dispatcher) {
        payerDao.delete(payerEntityMapper.toPayerEntity(payer))
    }

    override suspend fun deletePayers(payers: List<Payer>) = withContext(dispatcher) {
        payerDao.delete(payers.map { payer ->
            payerEntityMapper.toPayerEntity(payer)
        })
    }

    override suspend fun deletePayers() = withContext(dispatcher) {
        payerDao.deleteAll()
    }


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
