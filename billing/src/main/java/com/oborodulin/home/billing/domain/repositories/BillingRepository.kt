package com.oborodulin.home.billing.domain.repositories

import com.oborodulin.home.billing.domain.model.PayerDebt
import com.oborodulin.home.billing.domain.model.PayerServiceSubtotal
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface BillingRepository {
    fun getSubtotalDebts(payerId: UUID): Flow<List<PayerServiceSubtotal>>
    fun getTotalDebts(): Flow<List<PayerDebt>>
    fun getTotalDebt(payerId: UUID): Flow<PayerDebt>
}