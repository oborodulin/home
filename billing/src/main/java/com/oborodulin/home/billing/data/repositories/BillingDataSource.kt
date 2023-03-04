package com.oborodulin.home.billing.data.repositories

import com.oborodulin.home.billing.domain.model.Rate
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.servicing.domain.model.Service
import kotlinx.coroutines.flow.Flow
import java.util.*

interface BillingDataSource {
    fun getRates(): Flow<List<Rate>>
    fun getRate(id: UUID): Flow<Rate>
    fun getPayerRates(payerId: UUID): Flow<List<Rate>>
    fun getServiceRates(serviceId: UUID): Flow<List<Rate>>
    fun getPayerServiceRates(payerServiceId: UUID): Flow<List<Rate>>
    fun getServiceSubtotalDebts(payerId: UUID): Flow<List<Service>>
    fun getServiceTotalDebts(): Flow<List<Payer>>
    fun getServiceTotalDebt(payerId: UUID): Flow<Payer>
    suspend fun saveRate(rate: Rate)
    suspend fun deleteRate(rate: Rate)
    suspend fun deleteRate(rateId: UUID)
    suspend fun deleteRates(rates: List<Rate>)
    suspend fun deleteRates()
}