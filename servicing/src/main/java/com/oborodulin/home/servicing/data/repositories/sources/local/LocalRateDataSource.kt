package com.oborodulin.home.servicing.data.repositories.sources.local

import com.oborodulin.home.data.local.db.entities.RateEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface LocalRateDataSource {
    fun getRates(): Flow<List<RateEntity>>
    fun getRate(id: UUID): Flow<RateEntity>
    fun getPayerRates(payerId: UUID): Flow<List<RateEntity>>
    fun getServiceRates(serviceId: UUID): Flow<List<RateEntity>>
    fun getPayerServiceRates(payerServiceId: UUID): Flow<List<RateEntity>>
    suspend fun insertRate(rate: RateEntity)
    suspend fun updateRate(rate: RateEntity)
    suspend fun deleteRate(rate: RateEntity)
    suspend fun deleteRate(rateId: UUID)
    suspend fun deleteRates(rates: List<RateEntity>)
    suspend fun deleteRates()
}