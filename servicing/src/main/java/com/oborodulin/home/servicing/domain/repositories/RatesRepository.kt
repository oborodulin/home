package com.oborodulin.home.servicing.domain.repositories

import com.oborodulin.home.servicing.domain.model.Rate
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface RatesRepository {
    fun getAll(): Flow<List<Rate>>
    fun get(id: UUID): Flow<Rate>
    fun getByPayer(payerId: UUID): Flow<List<Rate>>
    fun getByService(serviceId: UUID): Flow<List<Rate>>
    fun getByPayerService(payerServiceId: UUID): Flow<List<Rate>>
    fun save(rate: Rate): Flow<Rate>
    fun delete(rate: Rate): Flow<Rate>
    fun delete(rateId: UUID): Flow<UUID>
    fun delete(rates: List<Rate>): Flow<List<Rate>>
    suspend fun deleteAll()
}