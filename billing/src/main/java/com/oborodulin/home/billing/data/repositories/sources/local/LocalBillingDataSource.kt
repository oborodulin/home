package com.oborodulin.home.billing.data.repositories.sources.local

import com.oborodulin.home.data.local.db.views.PayerServiceSubtotalDebtView
import com.oborodulin.home.data.local.db.views.PayerTotalDebtView
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface LocalBillingDataSource {
    fun getServiceSubtotalDebts(payerId: UUID): Flow<List<PayerServiceSubtotalDebtView>>
    fun getServiceTotalDebts(): Flow<List<PayerTotalDebtView>>
    fun getServiceTotalDebt(payerId: UUID): Flow<PayerTotalDebtView>
}