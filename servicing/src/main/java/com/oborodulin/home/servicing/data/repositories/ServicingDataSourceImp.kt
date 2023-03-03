package com.oborodulin.home.servicing.data.repositories

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.ServiceDao
import com.oborodulin.home.servicing.data.mappers.*
import com.oborodulin.home.servicing.domain.model.Service
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

/**
 * Created by tfakioglu on 12.December.2021
 */
class ServicingDataSourceImp @Inject constructor(
    private val serviceDao: ServiceDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val serviceViewListToServiceListMapper: ServiceViewListToServiceListMapper,
    private val serviceViewToServiceMapper: ServiceViewToServiceMapper,
    private val payerServiceViewToServiceMapper: PayerServiceViewToServiceMapper,
    private val payerServiceViewListToServiceListMapper: PayerServiceViewListToServiceListMapper,
    private val payerServiceSubtotalDebtViewListToServiceListMapper: PayerServiceSubtotalDebtViewListToServiceListMapper,
    private val payerTotalDebtViewToPayerMapper: PayerTotalDebtViewToPayerMapper,
    private val payerTotalDebtViewListToPayerListMapper: PayerTotalDebtViewListToPayerListMapper,
    private val serviceToServiceEntityMapper: ServiceToServiceEntityMapper,
    private val serviceToServiceTlEntityMapper: ServiceToServiceTlEntityMapper,
) : ServicingDataSource {
    override fun getServices() = serviceDao.findDistinctAll()
        .map(serviceViewListToServiceListMapper::map)

    override fun getService(id: UUID) = serviceDao.findDistinctById(id)
        .map(serviceViewToServiceMapper::map)

    override fun getMeterAllowedServices() = serviceDao.findMeterAllowed()
        .map(serviceViewListToServiceListMapper::map)

    override fun getPayerServices(payerId: UUID) = serviceDao.findByPayerId(payerId)
        .map(payerServiceViewListToServiceListMapper::map)

    override fun getPayerService(payerServiceId: UUID) =
        serviceDao.findPayerServiceById(payerServiceId).map(payerServiceViewToServiceMapper::map)

    override fun getServiceSubtotalDebts(payerId: UUID) =
        serviceDao.findSubtotalDebtsByPayerId(payerId)
            .map(payerServiceSubtotalDebtViewListToServiceListMapper::map)

    override fun getServiceTotalDebts() =
        serviceDao.findTotalDebts().map(payerTotalDebtViewListToPayerListMapper::map)

    override fun getServiceTotalDebt(payerId: UUID) = serviceDao.findTotalDebtByPayerId(payerId)
        .map(payerTotalDebtViewToPayerMapper::map)

    override suspend fun saveService(service: Service) = withContext(dispatcher) {
        if (service.id == null) {
            serviceDao.insert(
                serviceToServiceEntityMapper.map(service),
                serviceToServiceTlEntityMapper.map(service)
            )
        } else {
            serviceDao.update(
                serviceToServiceEntityMapper.map(service),
                serviceToServiceTlEntityMapper.map(service)
            )
        }
    }

    override suspend fun deleteService(service: Service) = withContext(dispatcher) {
        serviceDao.delete(serviceToServiceEntityMapper.map(service))
    }

    override suspend fun deleteService(serviceId: UUID) = withContext(dispatcher) {
        serviceDao.deleteById(serviceId)
    }

    override suspend fun deleteServices(services: List<Service>) = withContext(dispatcher) {
        serviceDao.delete(services.map(serviceToServiceEntityMapper::map))
    }

    override suspend fun deleteServices() = withContext(dispatcher) {
        serviceDao.deleteAll()
    }
}
