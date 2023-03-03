package com.oborodulin.home.servicing.domain.repositories

import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.servicing.domain.model.Service
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ServicesRepository {
    fun getAll(): Flow<List<Service>>
    fun get(id: UUID): Flow<Service>
    fun getMeterAllowed(): Flow<List<Service>>
    fun getPayerServices(payerId: UUID): Flow<List<Service>>
    fun getPayerService(payerServiceId: UUID): Flow<Service>
    fun getSubtotalDebts(payerId: UUID): Flow<List<Service>>
    fun getTotalDebts(): Flow<List<Payer>>
    fun getTotalDebt(payerId: UUID): Flow<Payer>
    fun save(service: Service): Flow<Service>
    fun delete(service: Service): Flow<Service>
    fun delete(serviceId: UUID): Flow<UUID>
    suspend fun deleteAll()
}