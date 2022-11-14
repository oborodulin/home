package com.oborodulin.home.metering.data.repositories

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.MeterDao
import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.data.local.db.entities.MeterValueEntity
import com.oborodulin.home.data.local.db.entities.MeterVerificationEntity
import com.oborodulin.home.data.local.db.entities.pojo.MeterPojo
import com.oborodulin.home.data.local.db.entities.pojo.PrevServiceMeterValuePojo
import com.oborodulin.home.metering.data.mappers.MeterPojoMapper
import com.oborodulin.home.metering.domain.model.Meter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

/**
 * Created by tfakioglu on 12.December.2021
 */
class MeteringDataSourceImpl @Inject constructor(
    private val meterDao: MeterDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val meterPojoMapper: MeterPojoMapper
) : MeteringDataSource {
    override fun getMeters() = meterDao.getMetersDistinctUntilChanged()

    override fun getMeter(id: UUID) = meterDao.getMeterDistinctUntilChanged(id)

    override fun getMeters(payerId: UUID) = meterDao.getMeters(payerId)

    override fun getMeterAndValues(payerId: UUID) = meterDao.getMeterAndValues(payerId)

    override fun getMeterAndVerifications(payerId: UUID) =
        meterDao.getMeterAndVerifications(payerId)

    override fun getPrevServiceMeterValues(payerId: UUID): Flow<List<PrevServiceMeterValuePojo>> =
        meterDao.getPrevMetersValuesByPayerDistinctUntilChanged(payerId)

    override suspend fun addMeter(meter: Meter) = withContext(dispatcher) {
        meterDao.add(meterPojoMapper.toMeterEntity(meter))
    }

    override suspend fun updateMeter(meter: Meter) = withContext(dispatcher) {
        meterDao.update(
            meterPojoMapper.toMeterEntity(meter),
            meterPojoMapper.toMeterTlEntity(meter)
        )
    }

    override suspend fun saveMeter(meter: Meter) = withContext(dispatcher) {
        meterDao.insert(
            meterPojoMapper.toMeterEntity(meter),
            meterPojoMapper.toMeterTlEntity(meter)
        )
    }

    override suspend fun deleteMeter(meter: Meter) = withContext(dispatcher) {
        meterDao.delete(meterPojoMapper.toMeterEntity(meter))
    }

    override suspend fun deleteMeters(meters: List<Meter>) = withContext(dispatcher) {
        meterDao.delete(meters.map { meter ->
            meterPojoMapper.toMeterEntity(meter)
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
