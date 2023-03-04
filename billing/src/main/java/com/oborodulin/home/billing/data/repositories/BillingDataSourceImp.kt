package com.oborodulin.home.billing.data.repositories

import com.oborodulin.home.billing.data.mappers.*
import com.oborodulin.home.billing.domain.model.Rate
import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.RateDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

/**
 * Created by tfakioglu on 12.December.2021
 */
class BillingDataSourceImp @Inject constructor(
    private val rateDao: RateDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val rateEntityListToRateListMapper: RateEntityListToRateListMapper,
    private val rateEntityToRateMapper: RateEntityToRateMapper,
    private val payerServiceSubtotalDebtViewListToServiceListMapper: PayerServiceSubtotalDebtViewListToServiceListMapper,
    private val payerTotalDebtViewToPayerMapper: PayerTotalDebtViewToPayerMapper,
    private val payerTotalDebtViewListToPayerListMapper: PayerTotalDebtViewListToPayerListMapper,
    private val rateToRateEntityMapper: RateToRateEntityMapper
) : BillingDataSource {
    override fun getRates() = rateDao.findDistinctAll()
        .map(rateEntityListToRateListMapper::map)

    override fun getRate(id: UUID) = rateDao.findDistinctById(id)
        .map(rateEntityToRateMapper::map)

    override fun getPayerRates(payerId: UUID) = rateDao.findByPayerId(payerId)
        .map(rateEntityListToRateListMapper::map)

    override fun getServiceRates(serviceId: UUID) = rateDao.findByServiceId(serviceId)
        .map(rateEntityListToRateListMapper::map)

    override fun getPayerServiceRates(payerServiceId: UUID) =
        rateDao.findByPayerServiceId(payerServiceId)
            .map(rateEntityListToRateListMapper::map)

    override fun getServiceSubtotalDebts(payerId: UUID) =
        rateDao.findSubtotalDebtsByPayerId(payerId)
            .map(payerServiceSubtotalDebtViewListToServiceListMapper::map)

    override fun getServiceTotalDebts() =
        rateDao.findTotalDebts().map(payerTotalDebtViewListToPayerListMapper::map)

    override fun getServiceTotalDebt(payerId: UUID) = rateDao.findTotalDebtByPayerId(payerId)
        .map(payerTotalDebtViewToPayerMapper::map)

    override suspend fun saveRate(rate: Rate) = withContext(dispatcher) {
        if (rate.id == null) {
            rateDao.insert(rateToRateEntityMapper.map(rate))
        } else {
            rateDao.update(rateToRateEntityMapper.map(rate))
        }
    }

    override suspend fun deleteRate(rate: Rate) = withContext(dispatcher) {
        rateDao.delete(rateToRateEntityMapper.map(rate))
    }

    override suspend fun deleteRate(rateId: UUID) = withContext(dispatcher) {
        rateDao.deleteById(rateId)
    }

    override suspend fun deleteRates(rates: List<Rate>) = withContext(dispatcher) {
        rateDao.delete(rates.map(rateToRateEntityMapper::map))
    }

    override suspend fun deleteRates() = withContext(dispatcher) {
        rateDao.deleteAll()
    }
}
