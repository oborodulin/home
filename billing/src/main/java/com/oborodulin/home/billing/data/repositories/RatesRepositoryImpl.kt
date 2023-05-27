package com.oborodulin.home.billing.data.repositories

import com.oborodulin.home.billing.domain.model.Rate
import com.oborodulin.home.billing.domain.repositories.RatesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class RatesRepositoryImpl @Inject constructor(
    private val billingDataSource: BillingDataSource
) : RatesRepository {
    override fun getAll() = billingDataSource.getRates()

    override fun get(id: UUID) = billingDataSource.getRate(id)

    override fun getByPayer(payerId: UUID) = billingDataSource.getPayerRates(payerId)

    override fun getByService(serviceId: UUID) =
        billingDataSource.getServiceRates(serviceId)

    override fun getByPayerService(payerServiceId: UUID) =
        billingDataSource.getPayerServiceRates(payerServiceId)

    override fun getSubtotalDebts(payerId: UUID) =
        billingDataSource.getServiceSubtotalDebts(payerId)

    override fun getTotalDebts() = billingDataSource.getServiceTotalDebts()

    override fun getTotalDebt(payerId: UUID) = billingDataSource.getServiceTotalDebt(payerId)

    override fun save(rate: Rate): Flow<Rate> = flow {
        billingDataSource.saveRate(rate)
        emit(rate)
    }

    override fun delete(rate: Rate): Flow<Rate> = flow {
        billingDataSource.deleteRate(rate)
        this.emit(rate)
    }

    override fun delete(serviceId: UUID): Flow<UUID> = flow {
        billingDataSource.deleteRate(serviceId)
        this.emit(serviceId)
    }

    override suspend fun deleteAll() = billingDataSource.deleteRates()
}