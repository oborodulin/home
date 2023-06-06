package com.oborodulin.home.servicing.domain.repositories

import com.oborodulin.home.servicing.domain.model.PayerService
import com.oborodulin.home.servicing.domain.model.Service
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ServicesRepository {
    // Services:
    fun getAllServices(): Flow<List<Service>>
    fun getService(id: UUID): Flow<Service>
    fun getMeterAllowedServices(): Flow<List<Service>>
    fun saveService(service: Service): Flow<Service>
    fun deleteService(service: Service): Flow<Service>
    fun deleteService(serviceId: UUID): Flow<UUID>
    suspend fun deleteAllServices()

    // Payer Services:
    fun getPayerServices(payerId: UUID): Flow<List<PayerService>>
    fun getPayerService(payerServiceId: UUID): Flow<PayerService>
    fun savePayerService(payerService: PayerService): Flow<PayerService>
    fun deletePayerService(payerService: PayerService): Flow<PayerService>
    fun deletePayerService(payerServiceId: UUID): Flow<UUID>
    suspend fun deleteAllPayerServices(payerId: UUID)
}