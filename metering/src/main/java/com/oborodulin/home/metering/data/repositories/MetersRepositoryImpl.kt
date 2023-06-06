package com.oborodulin.home.metering.data.repositories

import com.oborodulin.home.data.local.db.views.MeterValuePrevPeriodView
import com.oborodulin.home.metering.data.mappers.MeterMappers
import com.oborodulin.home.metering.data.repositories.sources.local.LocalMeteringDataSource
import com.oborodulin.home.metering.domain.model.Meter
import com.oborodulin.home.metering.domain.model.MeterValue
import com.oborodulin.home.metering.domain.repositories.MetersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class MetersRepositoryImpl @Inject constructor(
    private val localMeteringDataSource: LocalMeteringDataSource,
    private val mappers: MeterMappers
) : MetersRepository {
    override fun getAll() =
        localMeteringDataSource.getMeters().map(mappers.meterViewToMeterListMapper::map)

    override fun get(id: UUID) =
        localMeteringDataSource.getMeter(id).map(mappers.meterViewToMeterMapper::map)

    override fun getMeters(payerId: UUID) =
        localMeteringDataSource.getMeters(payerId).map(mappers.meterViewToMeterListMapper::map)

    override fun getMeterValues(meterId: UUID) = localMeteringDataSource.getMeterValues(meterId)
        .map(mappers.meterValueEntityListToMeterValueListMapper::map)

    override fun getMeterVerifications(meterId: UUID) =
        localMeteringDataSource.getMeterVerifications(meterId)
            .map(mappers.meterVerificationEntityListToMeterVerificationListMapper::map)

    override fun getPrevServiceMeterValues(payerId: UUID?): Flow<List<MeterValuePrevPeriodView>> =
        localMeteringDataSource.getPrevServiceMeterValues(payerId)

    override fun save(meter: Meter): Flow<Meter> = flow {
        if (meter.id == null) {
            localMeteringDataSource.insertMeter(
                mappers.meterToMeterEntityMapper.map(meter),
                mappers.meterToMeterTlEntityMapper.map(meter)
            )
        } else {
            localMeteringDataSource.updateMeter(
                mappers.meterToMeterEntityMapper.map(meter),
                mappers.meterToMeterTlEntityMapper.map(meter)
            )
        }
        emit(meter)
    }

    override fun save(meterValue: MeterValue): Flow<MeterValue> = flow {
        if (meterValue.id == null) {
            localMeteringDataSource.insertMeterValue(
                mappers.meterValueToMeterValueEntityMapper.map(meterValue)
            )
        } else {
            localMeteringDataSource.updateMeterValue(
                mappers.meterValueToMeterValueEntityMapper.map(meterValue)
            )
        }
        emit(meterValue)
    }

    override fun delete(meter: Meter): Flow<Meter> = flow {
        localMeteringDataSource.deleteMeter(mappers.meterToMeterEntityMapper.map(meter))
        this.emit(meter)
    }

    override suspend fun deleteMeters(meters: List<Meter>): Flow<List<Meter>> = flow {
        localMeteringDataSource.deleteMeters(meters.map(mappers.meterToMeterEntityMapper::map))
        this.emit(meters)
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