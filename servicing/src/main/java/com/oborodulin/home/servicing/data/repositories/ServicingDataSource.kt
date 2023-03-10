package com.oborodulin.home.servicing.data.repositories

import com.oborodulin.home.servicing.domain.model.PayerService
import com.oborodulin.home.servicing.domain.model.Service
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ServicingDataSource {
    fun getServices(): Flow<List<Service>>
    fun getService(id: UUID): Flow<Service>
    fun getMeterAllowedServices(): Flow<List<Service>>
    fun getPayerServices(payerId: UUID): Flow<List<PayerService>>
    fun getPayerService(payerServiceId: UUID): Flow<Service>
    suspend fun saveService(service: Service)
    suspend fun deleteService(service: Service)
    suspend fun deleteService(serviceId: UUID)
    suspend fun deleteServices(services: List<Service>)
    suspend fun deleteServices()
}