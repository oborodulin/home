package com.oborodulin.home.servicing.data.repositories

import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.servicing.domain.model.Service
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ServicingDataSource {
    fun getServices(): Flow<List<Service>>
    fun getService(id: UUID): Flow<Service>
    fun getMeterAllowedServices(): Flow<List<Service>>
    fun getPayerServices(payerId: UUID): Flow<List<Service>>
    fun getPayerService(payerServiceId: UUID): Flow<Service>
    fun getServiceSubtotalDebts(payerId: UUID): Flow<List<Service>>
    fun getServiceTotalDebts(): Flow<List<Payer>>
    fun getServiceTotalDebt(payerId: UUID): Flow<Payer>
    suspend fun saveService(service: Service)
    suspend fun deleteService(service: Service)
    suspend fun deleteService(serviceId: UUID)
    suspend fun deleteServices(services: List<Service>)
    suspend fun deleteServices()
}