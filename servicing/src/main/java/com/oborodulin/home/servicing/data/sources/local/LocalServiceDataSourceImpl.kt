package com.oborodulin.home.servicing.data.sources.local

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.dao.ServiceDao
import com.oborodulin.home.data.local.db.entities.PayerServiceCrossRefEntity
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.data.local.db.entities.ServiceTlEntity
import com.oborodulin.home.servicing.data.repositories.sources.local.LocalServiceDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

/**
 * Created by tfakioglu on 12.December.2021
 */
class LocalServiceDataSourceImpl @Inject constructor(
    private val serviceDao: ServiceDao,
    private val payerDao: PayerDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : LocalServiceDataSource {
    //For Services:
    override fun getServices() = serviceDao.findDistinctAll()

    override fun getService(id: UUID) = serviceDao.findDistinctById(id)

    override fun getMeterAllowedServices() = serviceDao.findMeterAllowed()
    override suspend fun insertService(service: ServiceEntity, textContent: ServiceTlEntity) =
        withContext(dispatcher) {
            serviceDao.insert(service, textContent)
        }

    override suspend fun updateService(service: ServiceEntity, textContent: ServiceTlEntity) =
        withContext(dispatcher) {
            serviceDao.update(service, textContent)
        }

    override suspend fun deleteService(service: ServiceEntity) = withContext(dispatcher) {
        serviceDao.delete(service)
    }

    override suspend fun deleteService(serviceId: UUID) = withContext(dispatcher) {
        serviceDao.deleteById(serviceId)
    }

    override suspend fun deleteServices(services: List<ServiceEntity>) = withContext(dispatcher) {
        serviceDao.delete(services)
    }

    override suspend fun deleteAllServices() = withContext(dispatcher) {
        serviceDao.deleteAll()
    }

    //For Payer Services:
    override fun getPayerServices(payerId: UUID) = serviceDao.findByPayerId(payerId)

    override fun getPayerService(payerServiceId: UUID) =
        serviceDao.findPayerServiceById(payerServiceId)

    override suspend fun insertPayerService(payerService: PayerServiceCrossRefEntity) =
        withContext(dispatcher) {
            payerDao.insert(payerService)
        }

    override suspend fun updatePayerService(payerService: PayerServiceCrossRefEntity) =
        withContext(dispatcher) {
            payerDao.update(payerService)
        }

    override suspend fun deletePayerService(payerService: PayerServiceCrossRefEntity) =
        withContext(dispatcher) {
            payerDao.deleteService(payerService)
        }

    override suspend fun deletePayerService(payerServiceId: UUID) = withContext(dispatcher) {
        payerDao.deleteServiceById(payerServiceId)
    }

    override suspend fun deletePayerServices(payerId: UUID) = withContext(dispatcher) {
        payerDao.deleteServicesByPayerId(payerId)
    }
}
