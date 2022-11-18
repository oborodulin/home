package com.oborodulin.home.metering.data.repositories

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.MeterDao
import com.oborodulin.home.metering.data.mappers.MeterMapper
import com.oborodulin.home.metering.domain.model.Meter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

/**
 * Created by tfakioglu on 12.December.2021
 */
class MeteringDataSourceImpl @Inject constructor(
    private val meterDao: MeterDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val meterMapper: MeterMapper
) : MeteringDataSource {
    override fun getMeters() = meterDao.findAllDistinctUntilChanged()
        .map { list ->
            list.map {
                meterMapper.toMeter(it)
            }
        }

    override fun getMeters(payerId: UUID) = meterDao.findByPayerId(payerId)
        .map { list ->
            list.map {
                meterMapper.toMeter(it)
            }
        }

    override fun getMeter(id: UUID) = meterDao.findByIdDistinctUntilChanged(id)
        .map {
            meterMapper.toMeter(it)
        }

    override fun getMeterValues(meterId: UUID) = meterDao.findValuesByMeterId(meterId)
        .map { list ->
            list.map {
                meterMapper.toMeterValue(it)
            }
        }

    override fun getMeterVerifications(meterId: UUID) =
        meterDao.findVerificationsByMeterId(meterId)
            .map { list ->
                list.map {
                    meterMapper.toMeterVerification(it)
                }
            }

    override fun getPrevServiceMeterValues(payerId: UUID) =
        meterDao.findPrevMetersValuesByPayerIdDistinctUntilChanged(payerId)

    override suspend fun saveMeter(meter: Meter) = withContext(dispatcher) {
        meterDao.insert(
            meterMapper.toMeterEntity(meter),
            meterMapper.toMeterTlEntity(meter)
        )
    }

    override suspend fun deleteMeter(meter: Meter) = withContext(dispatcher) {
        meterDao.delete(meterMapper.toMeterEntity(meter))
    }

    override suspend fun deleteMeters(meters: List<Meter>) = withContext(dispatcher) {
        meterDao.delete(meters.map { meter ->
            meterMapper.toMeterEntity(meter)
        })
    }

    override suspend fun deleteMeters() = withContext(dispatcher) {
        meterDao.deleteAll()
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
