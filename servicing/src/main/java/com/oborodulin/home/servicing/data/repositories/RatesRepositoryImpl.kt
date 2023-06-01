package com.oborodulin.home.servicing.data.repositories

import com.oborodulin.home.servicing.data.mappers.RateMappers
import com.oborodulin.home.servicing.data.repositories.sources.local.LocalRateDataSource
import com.oborodulin.home.servicing.domain.model.Rate
import com.oborodulin.home.servicing.domain.repositories.RatesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class RatesRepositoryImpl @Inject constructor(
    private val localRateDataSource: LocalRateDataSource,
    private val rateMappers: RateMappers
) : RatesRepository {
    override fun getAll() =
        localRateDataSource.getRates().map(rateMappers.rateEntityListToRateListMapper::map)

    override fun get(id: UUID) =
        localRateDataSource.getRate(id).map(rateMappers.rateEntityToRateMapper::map)

    override fun getByPayer(payerId: UUID) =
        localRateDataSource.getPayerRates(payerId)
            .map(rateMappers.rateEntityListToRateListMapper::map)

    override fun getByService(serviceId: UUID) =
        localRateDataSource.getServiceRates(serviceId)
            .map(rateMappers.rateEntityListToRateListMapper::map)

    override fun getByPayerService(payerServiceId: UUID) =
        localRateDataSource.getPayerServiceRates(payerServiceId)
            .map(rateMappers.rateEntityListToRateListMapper::map)

    override fun save(rate: Rate): Flow<Rate> = flow {
        if (rate.id == null) {
            localRateDataSource.insertRate(rateMappers.rateToRateEntityMapper.map(rate))
        } else {
            localRateDataSource.updateRate(rateMappers.rateToRateEntityMapper.map(rate))
        }
        emit(rate)
    }

    override fun delete(rate: Rate): Flow<Rate> = flow {
        localRateDataSource.deleteRate(rateMappers.rateToRateEntityMapper.map(rate))
        this.emit(rate)
    }

    override fun delete(rateId: UUID): Flow<UUID> = flow {
        localRateDataSource.deleteRate(rateId)
        this.emit(rateId)
    }

    override fun delete(rates: List<Rate>) = flow {
        localRateDataSource.deleteRates(rates.map(rateMappers.rateToRateEntityMapper::map))
        this.emit(rates)
    }

    override suspend fun deleteAll() = localRateDataSource.deleteRates()
}