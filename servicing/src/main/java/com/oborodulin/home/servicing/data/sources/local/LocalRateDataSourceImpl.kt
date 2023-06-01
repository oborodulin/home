package com.oborodulin.home.servicing.data.sources.local

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.RateDao
import com.oborodulin.home.data.local.db.entities.RateEntity
import com.oborodulin.home.servicing.data.repositories.sources.local.LocalRateDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

/**
 * Created by tfakioglu on 12.December.2021
 */
class LocalRateDataSourceImpl @Inject constructor(
    private val rateDao: RateDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : LocalRateDataSource {
    override fun getRates() = rateDao.findDistinctAll()

    override fun getRate(id: UUID) = rateDao.findDistinctById(id)

    override fun getPayerRates(payerId: UUID) = rateDao.findByPayerId(payerId)

    override fun getServiceRates(serviceId: UUID) = rateDao.findByServiceId(serviceId)

    override fun getPayerServiceRates(payerServiceId: UUID) =
        rateDao.findByPayerServiceId(payerServiceId)

    override suspend fun insertRate(rate: RateEntity) = withContext(dispatcher) {
        rateDao.insert(rate)
    }

    override suspend fun updateRate(rate: RateEntity) = withContext(dispatcher) {
        rateDao.update(rate)
    }

    override suspend fun deleteRate(rate: RateEntity) =
        withContext(dispatcher) {
            rateDao.delete(rate)
        }

    override suspend fun deleteRate(rateId: UUID) = withContext(dispatcher) {
        rateDao.deleteById(rateId)
    }

    override suspend fun deleteRates(rates: List<RateEntity>) = withContext(dispatcher) {
        rateDao.delete(rates)
    }

    override suspend fun deleteRates() = withContext(dispatcher) {
        rateDao.deleteAll()
    }
}
