package com.oborodulin.home.servicing.data.sources.local

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.ServiceDao
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.data.local.db.views.ServiceView
import com.oborodulin.home.servicing.data.mappers.*
import com.oborodulin.home.servicing.data.repositories.sources.local.LocalServiceDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

/**
 * Created by tfakioglu on 12.December.2021
 */
class LocalServiceDataSourceImpl @Inject constructor(
    private val serviceDao: ServiceDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val serviceViewToServiceEntityMapper: ServiceViewToServiceEntityMapper,
    private val serviceViewToServiceTlEntityMapper: ServiceViewToServiceTlEntityMapper
) : LocalServiceDataSource {
    //For Services:
    override fun getServices() = serviceDao.findDistinctAll()

    override fun getService(id: UUID) = serviceDao.findDistinctById(id)

    override fun getMeterAllowedServices() = serviceDao.findMeterAllowed()
    override suspend fun insertService(service: ServiceView) = withContext(dispatcher) {
        serviceDao.insert(
            serviceViewToServiceEntityMapper.map(service),
            serviceViewToServiceTlEntityMapper.map(service)
        )
    }

    override suspend fun updateService(service: ServiceView) = withContext(dispatcher) {
        serviceDao.update(
            serviceViewToServiceEntityMapper.map(service),
            serviceViewToServiceTlEntityMapper.map(service)
        )
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
        serviceDao.findPayerServiceById(payerServiceId).map(payerServiceViewToServiceMapper::map)
}
