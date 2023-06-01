package com.oborodulin.home.billing.data.sources.local

import com.oborodulin.home.billing.data.repositories.sources.local.LocalBillingDataSource
import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.RateDao
import kotlinx.coroutines.CoroutineDispatcher
import java.util.UUID
import javax.inject.Inject

/**
 * Created by tfakioglu on 12.December.2021
 */
class LocalBillingDataSourceImpl @Inject constructor(
    private val rateDao: RateDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : LocalBillingDataSource {

    override fun getServiceSubtotalDebts(payerId: UUID) =
        rateDao.findSubtotalDebtsByPayerId(payerId)

    override fun getServiceTotalDebts() = rateDao.findTotalDebts()

    override fun getServiceTotalDebt(payerId: UUID) = rateDao.findTotalDebtByPayerId(payerId)
}
