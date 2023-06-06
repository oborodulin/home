package com.oborodulin.home.servicing.data.repositories

import com.oborodulin.home.servicing.data.mappers.PayerServiceMappers
import com.oborodulin.home.servicing.data.mappers.ServiceMappers
import com.oborodulin.home.servicing.data.repositories.sources.local.LocalServiceDataSource
import com.oborodulin.home.servicing.domain.model.PayerService
import com.oborodulin.home.servicing.domain.model.Service
import com.oborodulin.home.servicing.domain.repositories.ServicesRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class ServicesRepositoryImpl @Inject constructor(
    private val localServiceDataSource: LocalServiceDataSource,
    private val serviceMappers: ServiceMappers,
    private val payerServiceMappers: PayerServiceMappers
) : ServicesRepository {
    // Services:
    override fun getAllServices() =
        localServiceDataSource.getServices()
            .map(serviceMappers.serviceViewListToServiceListMapper::map)

    override fun getService(id: UUID) =
        localServiceDataSource.getService(id)
            .map(serviceMappers.serviceViewToServiceMapper::map)

    override fun getMeterAllowedServices() =
        localServiceDataSource.getMeterAllowedServices()
            .map(serviceMappers.serviceViewListToServiceListMapper::map)

    override fun saveService(service: Service) = flow {
        if (service.id == null)
            localServiceDataSource.insertService(
                serviceMappers.serviceToServiceEntityMapper.map(service),
                serviceMappers.serviceToServiceTlEntityMapper.map(service)
            )
        else
            localServiceDataSource.updateService(
                serviceMappers.serviceToServiceEntityMapper.map(service),
                serviceMappers.serviceToServiceTlEntityMapper.map(service)
            )
        emit(service)
    }

    override fun deleteService(service: Service) = flow {
        localServiceDataSource.deleteService(serviceMappers.serviceToServiceEntityMapper.map(service))
        this.emit(service)
    }

    override fun deleteService(serviceId: UUID) = flow {
        localServiceDataSource.deleteService(serviceId)
        this.emit(serviceId)
    }

    override suspend fun deleteAllServices() = localServiceDataSource.deleteAllServices()

    // Payer Services:
    override fun getPayerServices(payerId: UUID) = localServiceDataSource.getPayerServices(payerId)
        .map(payerServiceMappers.payerServiceViewListToPayerServiceListMapper::map)

    override fun getPayerService(payerServiceId: UUID) =
        localServiceDataSource.getPayerService(payerServiceId)
            .map(payerServiceMappers.payerServiceViewToPayerServiceMapper::map)

    override fun savePayerService(payerService: PayerService) = flow {
        if (payerService.id == null)
            localServiceDataSource.insertPayerService(
                payerServiceMappers.payerServiceToPayerServiceCrossRefEntityMapper.map(payerService)
            )
        else
            localServiceDataSource.updatePayerService(
                payerServiceMappers.payerServiceToPayerServiceCrossRefEntityMapper.map(payerService)
            )
        emit(payerService)
    }

    override fun deletePayerService(payerService: PayerService) = flow {
        localServiceDataSource.deletePayerService(
            payerServiceMappers.payerServiceToPayerServiceCrossRefEntityMapper.map(payerService)
        )
        this.emit(payerService)
    }

    override fun deletePayerService(payerServiceId: UUID) = flow {
        localServiceDataSource.deletePayerService(payerServiceId)
        this.emit(payerServiceId)
    }

    override suspend fun deleteAllPayerServices(payerId: UUID) =
        localServiceDataSource.deletePayerServices(payerId)
}