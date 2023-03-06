package com.oborodulin.home.data.local.db

import android.database.sqlite.SQLiteConstraintException
import android.os.Build
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.dao.RateDao
import com.oborodulin.home.data.local.db.dao.ServiceDao
import com.oborodulin.home.data.local.db.entities.RateEntity
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.data.local.db.entities.ServiceTlEntity
import com.oborodulin.home.data.local.db.views.ServiceView
import com.oborodulin.home.data.util.ServiceType
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
@MediumTest
class RateDaoTest : HomeDatabaseTest() {
    private lateinit var rateDao: RateDao
    private lateinit var payerDao: PayerDao
    private lateinit var serviceDao: ServiceDao

    @Before
    override fun setUp() {
        super.setUp()
        rateDao = rateDao()
        payerDao = payerDao()
        serviceDao = serviceDao()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertComonRatesAndFindAll_shouldReturn_theRatesList_inFlow() = runTest {
        // ARRANGE
        ServiceDaoTest.insertService(appContext, serviceDao, ServiceEntity.rentService())
        val electricityServiceId = ServiceDaoTest.insertService(appContext, serviceDao, ServiceEntity.electricityService())
        // ACT
        rateDao.insert(RateEntity.electricityRateFrom0To150(electricityServiceId))
        rateDao.insert(RateEntity.electricityRateFrom150To800(electricityServiceId))
        rateDao.insert(RateEntity.electricityRateFrom800(electricityServiceId))
        rateDao.insert(RateEntity.electricityPrivilegesRate(electricityServiceId))
        // ASSERT
        rateDao.findDistinctAll().test {
            val services = awaitItem()
            assertThat(services).hasSize(2)
            assertThat(services[0].data).isEqualTo(rentService)
            assertThat(services[0].tl).isEqualTo(rentServiceTl)
            assertThat(services[1].data).isEqualTo(electricityService)
            assertThat(services[1].tl).isEqualTo(electricityServiceTl)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertPayerRatesAndFindAll_shouldReturn_theRatesList_inFlow() = runTest {
        // ARRANGE
        ServiceDaoTest.insertService(appContext, serviceDao, ServiceEntity.rentService())
        val electricityServiceId = ServiceDaoTest.insertService(appContext, serviceDao, ServiceEntity.electricityService())
        // ACT
        rateDao.insert(RateEntity.electricityRateFrom0To150(electricityServiceId))
        rateDao.insert(RateEntity.electricityRateFrom150To800(electricityServiceId))
        rateDao.insert(RateEntity.electricityRateFrom800(electricityServiceId))
        rateDao.insert(RateEntity.electricityPrivilegesRate(electricityServiceId))
        // ASSERT
        rateDao.findDistinctAll().test {
            val services = awaitItem()
            assertThat(services).hasSize(2)
            assertThat(services[0].data).isEqualTo(rentService)
            assertThat(services[0].tl).isEqualTo(rentServiceTl)
            assertThat(services[1].data).isEqualTo(electricityService)
            assertThat(services[1].tl).isEqualTo(electricityServiceTl)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertCommonAndPrivilegesRatesAndFindAll_shouldReturn_onlyPrivilegesRate_inFlow() = runTest {
        // ARRANGE
        ServiceDaoTest.insertService(appContext, serviceDao, ServiceEntity.rentService())
        val electricityServiceId = ServiceDaoTest.insertService(appContext, serviceDao, ServiceEntity.electricityService())
        // ACT
        rateDao.insert(RateEntity.electricityRateFrom0To150(electricityServiceId))
        rateDao.insert(RateEntity.electricityRateFrom150To800(electricityServiceId))
        rateDao.insert(RateEntity.electricityRateFrom800(electricityServiceId))
        rateDao.insert(RateEntity.electricityPrivilegesRate(electricityServiceId))
        // ASSERT
        rateDao.findDistinctAll().test {
            val services = awaitItem()
            assertThat(services).hasSize(2)
            assertThat(services[0].data).isEqualTo(rentService)
            assertThat(services[0].tl).isEqualTo(rentServiceTl)
            assertThat(services[1].data).isEqualTo(electricityService)
            assertThat(services[1].tl).isEqualTo(electricityServiceTl)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateServiceAndFindById_shouldReturn_theUpdatedService_inFlow() = runTest {
        // ARRANGE
        val rentService = ServiceEntity.rentService()
        val rentServiceTl = ServiceTlEntity.rentServiceTl(appContext, rentService.serviceId)
        rateDao.insert(rentService, rentServiceTl)
        val electricityService = ServiceEntity.electricityService(rentService.serviceId)
        val electricityServiceTl =
            ServiceTlEntity.electricityServiceTl(appContext, electricityService.serviceId)
        // ACT
        rateDao.update(electricityService, electricityServiceTl)
        // ASSERT
        rateDao.findDistinctById(rentService.serviceId).test {
            val service = awaitItem()
            assertThat(service).isNotNull()
            assertThat(service.data).isEqualTo(electricityService)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteServiceById_returnsIsNull() = runTest {
        // ARRANGE
        val rentService = ServiceEntity.rentService()
        val rentServiceTl = ServiceTlEntity.rentServiceTl(appContext, rentService.serviceId)
        rateDao.insert(rentService, rentServiceTl)
        // ACT
        rateDao.deleteById(rentService.serviceId)
        // ASSERT
        rateDao.findDistinctById(rentService.serviceId).test {
            assertThat(awaitItem()).isNull()
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteAllServicesAndFindAll_returnsIsEmpty() = runTest {
        // ARRANGE
        val rentService = ServiceEntity.rentService()
        val rentServiceTl = ServiceTlEntity.rentServiceTl(appContext, rentService.serviceId)
        val electricityService = ServiceEntity.electricityService()
        val electricityServiceTl =
            ServiceTlEntity.electricityServiceTl(appContext, electricityService.serviceId)
        rateDao.insert(rentService, rentServiceTl)
        rateDao.insert(electricityService, electricityServiceTl)
        // ACT
        rateDao.deleteAll()
        // ASSERT
        rateDao.findDistinctAll().test {
            assertThat(awaitItem()).isEmpty()
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun initiateServicePosAndFindAll_return_theSeqServicesPosOrdered_inFlow() = runTest {
        // 1. ARRANGE
        val service1 = ServiceEntity.defaultService(serviceType = ServiceType.RENT)
        val serviceTl1 = ServiceTlEntity.rentServiceTl(appContext, service1.serviceId)
        val service2 = ServiceEntity.defaultService(serviceType = ServiceType.INTERNET)
        val serviceTl2 = ServiceTlEntity.rentServiceTl(appContext, service2.serviceId)
        // 1. ACT
        rateDao.insert(service1, serviceTl1)
        rateDao.insert(service2, serviceTl2)
        // 2. ARRANGE
        val service3 =
            ServiceEntity.defaultService(servicePos = 2, serviceType = ServiceType.GARBAGE)
        val serviceTl3 = ServiceTlEntity.rentServiceTl(appContext, service3.serviceId)
        // 2. ACT
        rateDao.insert(service3, serviceTl3)
        // ASSERT
        rateDao.findDistinctAll().test {
            val services = awaitItem()
            assertThat(services).hasSize(3)
            assertThat(services[0].data).isEqualTo(service1)
            assertThat(services[0].data.servicePos).isEqualTo(1)
            assertThat(services[1].data).isEqualTo(service3)
            assertThat(services[1].data.servicePos).isEqualTo(2)
            assertThat(services[2].data).isEqualTo(service2)
            assertThat(services[2].data.servicePos).isEqualTo(3)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateServicePosAndFindAll_return_theSeqServicesPosOrdered_inFlow() = runTest {
        // ARRANGE
        val service1 = ServiceEntity.defaultService(serviceType = ServiceType.RENT)
        val serviceTl1 = ServiceTlEntity.rentServiceTl(appContext, service1.serviceId)
        val service2 = ServiceEntity.defaultService(serviceType = ServiceType.INTERNET)
        val serviceTl2 = ServiceTlEntity.rentServiceTl(appContext, service2.serviceId)
        val service3 = ServiceEntity.defaultService(serviceType = ServiceType.GARBAGE)
        val serviceTl3 = ServiceTlEntity.rentServiceTl(appContext, service3.serviceId)
        rateDao.insert(service1, serviceTl1)
        rateDao.insert(service2, serviceTl2)
        rateDao.insert(service3, serviceTl3)
        lateinit var testService: ServiceView
        rateDao.findDistinctById(service3.serviceId).test {
            testService = awaitItem()
        }
        // ACT
        rateDao.update(
            ServiceEntity.defaultService(
                serviceId = testService.data.serviceId,
                servicePos = 2,
                serviceType = testService.data.serviceType
            ), serviceTl3
        )
        // ASSERT
        rateDao.findDistinctAll().test {
            val services = awaitItem()
            assertThat(services).hasSize(3)
            assertThat(services[0].data).isEqualTo(service1)
            assertThat(services[0].data.servicePos).isEqualTo(1)
            assertThat(services[1].data).isEqualTo(service3)
            assertThat(services[1].data.servicePos).isEqualTo(2)
            assertThat(services[2].data).isEqualTo(service2)
            assertThat(services[2].data.servicePos).isEqualTo(3)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test(expected = SQLiteConstraintException::class)
    fun duplicateServiceType_ExceptionThrown() = runTest {
        // ARRANGE
        val service1 = ServiceEntity.defaultService(serviceType = ServiceType.RENT)
        val serviceTl1 = ServiceTlEntity.rentServiceTl(appContext, service1.serviceId)
        val service2 = ServiceEntity.defaultService(serviceType = ServiceType.RENT)
        val serviceTl2 = ServiceTlEntity.rentServiceTl(appContext, service2.serviceId)
        // ACT
        rateDao.insert(service1, serviceTl1)
        rateDao.insert(service2, serviceTl2)

    }

    companion object {
        suspend fun insertRate(
            rateDao: RateDao, rate: RateEntity = RateEntity.defaultRate()
        ): UUID {
            rateDao.insert(rate)
            return rate.payerId
        }
    }
}