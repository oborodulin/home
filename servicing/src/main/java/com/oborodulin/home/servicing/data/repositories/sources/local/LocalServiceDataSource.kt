package com.oborodulin.home.servicing.data.repositories.sources.local

import com.oborodulin.home.data.local.db.entities.PayerServiceCrossRefEntity
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.data.local.db.entities.ServiceTlEntity
import com.oborodulin.home.data.local.db.views.PayerServiceView
import com.oborodulin.home.data.local.db.views.ServiceView
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface LocalServiceDataSource {
    //For Services:
    fun getServices(): Flow<List<ServiceView>>
    fun getService(id: UUID): Flow<ServiceView>
    fun getMeterAllowedServices(): Flow<List<ServiceView>>
    suspend fun insertService(service: ServiceEntity, textContent: ServiceTlEntity)
    suspend fun updateService(service: ServiceEntity, textContent: ServiceTlEntity)
    suspend fun deleteService(service: ServiceEntity)
    suspend fun deleteService(serviceId: UUID)
    suspend fun deleteServices(services: List<ServiceEntity>)
    suspend fun deleteAllServices()

    //For Payer Services:
    fun getPayerServices(payerId: UUID): Flow<List<PayerServiceView>>
    fun getPayerService(payerServiceId: UUID): Flow<PayerServiceView>
    suspend fun insertPayerService(payerService: PayerServiceCrossRefEntity)
    suspend fun updatePayerService(payerService: PayerServiceCrossRefEntity)
    suspend fun deletePayerService(payerService: PayerServiceCrossRefEntity)
    suspend fun deletePayerService(payerServiceId: UUID)
    suspend fun deletePayerServices(payerId: UUID)
}