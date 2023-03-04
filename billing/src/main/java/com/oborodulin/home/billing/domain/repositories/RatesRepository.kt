package com.oborodulin.home.billing.domain.repositories

import com.oborodulin.home.billing.domain.model.Rate
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.servicing.domain.model.Service
import kotlinx.coroutines.flow.Flow
import java.util.*

interface RatesRepository {
    fun getAll(): Flow<List<Rate>>
    fun get(id: UUID): Flow<Rate>
    fun getByPayer(payerId: UUID): Flow<List<Rate>>
    fun getByService(serviceId: UUID): Flow<List<Rate>>
    fun getByPayerService(payerServiceId: UUID): Flow<List<Rate>>
    fun getSubtotalDebts(payerId: UUID): Flow<List<Service>>
    fun getTotalDebts(): Flow<List<Payer>>
    fun getTotalDebt(payerId: UUID): Flow<Payer>
    fun save(rate: Rate): Flow<Rate>
    fun delete(rate: Rate): Flow<Rate>
    fun delete(rateId: UUID): Flow<UUID>
    suspend fun deleteAll()
}