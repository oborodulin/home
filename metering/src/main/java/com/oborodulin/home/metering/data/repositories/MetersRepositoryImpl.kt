package com.oborodulin.home.metering.data.repositories

import com.oborodulin.home.data.local.db.views.MeterValuePrevPeriodView
import com.oborodulin.home.metering.data.repositories.sources.local.LocalMeteringDataSource
import com.oborodulin.home.metering.domain.model.Meter
import com.oborodulin.home.metering.domain.model.MeterValue
import com.oborodulin.home.metering.domain.repositories.MetersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class MetersRepositoryImpl @Inject constructor(
    private val localMeteringDataSource: LocalMeteringDataSource
) : MetersRepository {
    override fun getAll() = localMeteringDataSource.getMeters()

    override fun get(id: UUID) = localMeteringDataSource.getMeter(id)

    override fun getMeters(payerId: UUID) = localMeteringDataSource.getMeters(payerId)

    override fun getMeterValues(meterId: UUID) = localMeteringDataSource.getMeterValues(meterId)

    override fun getMeterVerifications(meterId: UUID) =
        localMeteringDataSource.getMeterVerifications(meterId)

    override fun getPrevServiceMeterValues(payerId: UUID?): Flow<List<MeterValuePrevPeriodView>> =
        localMeteringDataSource.getPrevServiceMeterValues(payerId)

    override fun save(meter: Meter): Flow<Meter> = flow {
        localMeteringDataSource.saveMeter(meter)
        emit(meter)
    }

    override fun save(meterValue: MeterValue): Flow<MeterValue> = flow {
        localMeteringDataSource.saveMeterValue(meterValue)
        emit(meterValue)
    }

    override fun delete(meter: Meter): Flow<Meter> = flow {
        localMeteringDataSource.deleteMeter(meter)
        this.emit(meter)
    }

    override suspend fun deleteAll() = localMeteringDataSource.deleteMeters()

    override fun deleteCurrentValue(meterId: UUID): Flow<UUID> = flow {
        localMeteringDataSource.deleteMeterCurrentValue(meterId)
        this.emit(meterId)
    }
    /*
        fun nowPlaying(): Flow<PagingData<NetworkMovie>> {
            val config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                prefetchDistance = 5
            )
            return Pager(config) {
                AccountingDataSource(
                    nowPlayingUseCase = accountingUseCase
                )
            }.flow
        }
      */
}