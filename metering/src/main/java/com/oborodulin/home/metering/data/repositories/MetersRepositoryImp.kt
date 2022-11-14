package com.oborodulin.home.metering.data.repositories

import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.data.local.db.entities.MeterValueEntity
import com.oborodulin.home.data.local.db.entities.MeterVerificationEntity
import com.oborodulin.home.data.local.db.entities.pojo.PrevServiceMeterValuePojo
import com.oborodulin.home.metering.domain.model.Meter
import com.oborodulin.home.metering.domain.repositories.MetersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class MetersRepositoryImp @Inject constructor(
    private val meteringDataSource: MeteringDataSource
) : MetersRepository {
    override fun getAll() = meteringDataSource.getMeters()

    override fun get(id: UUID) = meteringDataSource.getMeter(id)

    override fun getMeters(payerId: UUID) = meteringDataSource.getMeters(payerId)

    override fun getMeterAndValues(payerId: UUID) = meteringDataSource.getMeterAndValues(payerId)

    override fun getMeterAndVerifications(payerId: UUID) =
        meteringDataSource.getMeterAndVerifications(payerId)

    override fun getPrevServiceMeterValues(payerId: UUID): Flow<List<PrevServiceMeterValuePojo>> =
        meteringDataSource.getPrevServiceMeterValues(payerId)

    override fun add(meter: Meter): Flow<Meter> = flow {
        meteringDataSource.addMeter(meter)
        emit(meter)
    }

    override fun update(meter: Meter): Flow<Meter> = flow {
        meteringDataSource.updateMeter(meter)
        emit(meter)
    }

    override fun save(meter: Meter): Flow<Meter> = flow {
        meteringDataSource.saveMeter(meter)
        emit(meter)
    }

    override fun delete(meter: Meter): Flow<Meter> = flow {
        meteringDataSource.deleteMeter(meter)
        this.emit(meter)
    }

    override suspend fun deleteAll() = meteringDataSource.deleteMeters()

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