package com.oborodulin.home.servicing.data.repositories

import com.oborodulin.home.servicing.domain.model.Service
import com.oborodulin.home.servicing.domain.repositories.ServicesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class ServicesRepositoryImpl @Inject constructor(
    private val servicingDataSource: ServicingDataSource
) : ServicesRepository {
    override fun getAll() = servicingDataSource.getServices()

    override fun get(id: UUID) = servicingDataSource.getService(id)

    override fun getMeterAllowed() = servicingDataSource.getMeterAllowedServices()

    override fun getPayerServices(payerId: UUID) = servicingDataSource.getPayerServices(payerId)

    override fun getPayerService(payerServiceId: UUID) =
        servicingDataSource.getPayerService(payerServiceId)

    override fun save(service: Service): Flow<Service> = flow {
        servicingDataSource.saveService(service)
        emit(service)
    }

    override fun delete(service: Service): Flow<Service> = flow {
        servicingDataSource.deleteService(service)
        this.emit(service)
    }

    override fun deleteById(serviceId: UUID): Flow<UUID> = flow {
        servicingDataSource.deleteService(serviceId)
        this.emit(serviceId)
    }

    override suspend fun deleteAll() = servicingDataSource.deleteServices()
}