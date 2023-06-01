package com.oborodulin.home.billing.data.repositories

import com.oborodulin.home.billing.data.mappers.BillingMappers
import com.oborodulin.home.billing.data.repositories.sources.local.LocalBillingDataSource
import com.oborodulin.home.billing.domain.repositories.BillingRepository
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class BillingRepositoryImpl @Inject constructor(
    private val localBillingDataSource: LocalBillingDataSource,
    private val mappers: BillingMappers
) : BillingRepository {

    override fun getSubtotalDebts(payerId: UUID) =
        localBillingDataSource.getServiceSubtotalDebts(payerId)
            .map(mappers.payerServiceSubtotalDebtViewListToPayerServiceDebtListMapper::map)

    override fun getTotalDebts() = localBillingDataSource.getServiceTotalDebts()
        .map(mappers.payerTotalDebtViewListToPayerDebtListMapper::map)

    override fun getTotalDebt(payerId: UUID) = localBillingDataSource.getServiceTotalDebt(payerId)
        .map(mappers.payerTotalDebtViewToPayerDebtMapper::map)
}