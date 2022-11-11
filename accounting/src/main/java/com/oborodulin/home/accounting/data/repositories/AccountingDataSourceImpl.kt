package com.oborodulin.home.accounting.data.repositories

//import com.oborodulin.home.domain.model.NetworkMovie
import com.oborodulin.home.accounting.data.mappers.PayerEntityMapper
import com.oborodulin.home.accounting.domain.model.Payer
import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.entities.PayerEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

/**
 * Created by tfakioglu on 12.December.2021
 */
class AccountingDataSourceImpl @Inject constructor(
    private val payerDao: PayerDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val payerEntityMapper: PayerEntityMapper
/*private val nowPlayingUseCase: NowPlayingUseCase*/
) : AccountingDataSource
//    :    PagingSource<Int, NetworkMovie>()
{
    override fun getPayers(): Flow<List<PayerEntity>> = payerDao.getAll()
    /*{
        return payerDao.getAll()
            .map { list ->
            list.map {
                payerEntityMapper.toPayer(it)
            }
        }
    }*/

    override fun getPayer(id: UUID): Flow<PayerEntity> =
        payerDao.get(id)//.map { payerEntityMapper.toPayer(it) }

    override suspend fun addPayer(payer: Payer) = withContext(dispatcher) {
        payerDao.add(payerEntityMapper.toPayerEntity(payer))
    }

    override suspend fun updatePayer(payer: Payer) = withContext(dispatcher) {
        payerDao.update(payerEntityMapper.toPayerEntity(payer))
    }

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
