package com.oborodulin.home.servicing.data.repositories

import com.oborodulin.home.servicing.data.mappers.ServiceMappers
import com.oborodulin.home.servicing.data.mappers.ServiceViewMappers
import com.oborodulin.home.servicing.data.repositories.sources.local.LocalServiceDataSource
import com.oborodulin.home.servicing.domain.model.Service
import com.oborodulin.home.servicing.domain.repositories.ServicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class ServicesRepositoryImpl @Inject constructor(
    private val localServiceDataSource: LocalServiceDataSource,
    private val serviceViewMappers: ServiceViewMappers,
    private val serviceMappers: ServiceMappers
) : ServicesRepository {
    // Services:
    override fun getAllServices() =
        localServiceDataSource.getServices()
            .map(serviceViewMappers.serviceViewListToServiceListMapper::map)

    override fun getService(id: UUID) =
        localServiceDataSource.getService(id)
            .map(serviceViewMappers.serviceViewToServiceMapper::map)

    override fun getMeterAllowedServices() =
        localServiceDataSource.getMeterAllowedServices()
            .map(serviceViewMappers.serviceViewListToServiceListMapper::map)

    override fun saveService(service: Service): Flow<Service> = flow {
        if (service.id == null)
            localServiceDataSource.insertService(
                serviceViewMappers.serviceToServiceViewMapper.map(service)
            )
        else
            localServiceDataSource.updateService(
                serviceViewMappers.serviceToServiceViewMapper.map(service)
            )
        emit(service)
    }

    override fun deleteService(service: Service): Flow<Service> = flow {
        localServiceDataSource.deleteService(serviceMappers.serviceToServiceEntityMapper.map(service))
        this.emit(service)
    }

    override fun deleteService(serviceId: UUID): Flow<UUID> = flow {
        localServiceDataSource.deleteService(serviceId)
        this.emit(serviceId)
    }

    override suspend fun deleteAllServices() = localServiceDataSource.deleteAllServices()

    // Payer Services:
    override fun getPayerServices(payerId: UUID) = localServiceDataSource.getPayerServices(payerId)
        .map(payerServiceViewListToPayerServiceListMapper::map)

    override fun getPayerService(payerServiceId: UUID) =
        localServiceDataSource.getPayerService(payerServiceId)
}