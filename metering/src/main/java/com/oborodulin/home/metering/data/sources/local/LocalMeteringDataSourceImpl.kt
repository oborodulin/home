package com.oborodulin.home.metering.data.sources.local

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.MeterDao
import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.data.local.db.entities.MeterTlEntity
import com.oborodulin.home.data.local.db.entities.MeterValueEntity
import com.oborodulin.home.metering.data.repositories.sources.local.LocalMeteringDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

/**
 * Created by tfakioglu on 12.December.2021
 */
class LocalMeteringDataSourceImpl @Inject constructor(
    private val meterDao: MeterDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : LocalMeteringDataSource {
    override fun getMeters() = meterDao.findDistinctAll()

    override fun getMeters(payerId: UUID) = meterDao.findByPayerId(payerId)

    override fun getMeter(id: UUID) = meterDao.findDistinctById(id)

    override fun getMeterValues(meterId: UUID) = meterDao.findValuesByMeterId(meterId)

    override fun getMeterVerifications(meterId: UUID) = meterDao.findVerificationsByMeterId(meterId)

    override fun getPrevServiceMeterValues(payerId: UUID?) =
        when (payerId) {
            null -> meterDao.findPrevMetersValuesByFavoritePayer()
            else -> meterDao.findDistinctPrevMetersValuesByPayerId(payerId)
        }

    override suspend fun insertMeter(meter: MeterEntity, textContent: MeterTlEntity) =
        withContext(dispatcher) {
            meterDao.insert(meter, textContent)
        }

    override suspend fun updateMeter(meter: MeterEntity, textContent: MeterTlEntity) =
        withContext(dispatcher) {
            meterDao.update(meter, textContent)
        }

    override suspend fun insertMeterValue(meterValue: MeterValueEntity) = withContext(dispatcher) {
        meterDao.insert(meterValue)
    }

    override suspend fun updateMeterValue(meterValue: MeterValueEntity) = withContext(dispatcher) {
        meterDao.update(meterValue)
    }

    override suspend fun deleteMeter(meter: MeterEntity) = withContext(dispatcher) {
        meterDao.delete(meter)
    }

    override suspend fun deleteMeters(meters: List<MeterEntity>) = withContext(dispatcher) {
        meterDao.delete(meters)
    }

    override suspend fun deleteMeters() = withContext(dispatcher) {
        meterDao.deleteAll()
    }

    override suspend fun deleteMeterCurrentValue(meterId: UUID) = withContext(dispatcher) {
        meterDao.deleteCurrentValue(meterId)
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
