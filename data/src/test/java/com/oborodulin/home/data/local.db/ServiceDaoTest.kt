package com.oborodulin.home.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.os.Build
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.oborodulin.home.data.local.db.dao.ServiceDao
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
@SmallTest
class ServiceDaoTest : HomeDatabaseTest() {
    private lateinit var serviceDao: ServiceDao

    @Before
    override fun setUp() {
        super.setUp()
        serviceDao = serviceDao()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertServicesAndFindAll_shouldReturn_theOrderedItem_inFlow() = runTest {
        // ARRANGE
        val rentService = ServiceEntity.rentService()
        val rentServiceTl = ServiceTlEntity.rentServiceTl(ctx, rentService.serviceId)
        val electricityService = ServiceEntity.electricityService()
        val electricityServiceTl =
            ServiceTlEntity.electricityServiceTl(ctx, electricityService.serviceId)
        // ACT
        serviceDao.insert(rentService, rentServiceTl)
        serviceDao.insert(electricityService, electricityServiceTl)
        // ASSERT
        serviceDao.findDistinctAll().test {
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
        val rentServiceTl = ServiceTlEntity.rentServiceTl(ctx, rentService.serviceId)
        serviceDao.insert(rentService, rentServiceTl)
        val electricityService = ServiceEntity.electricityService(rentService.serviceId)
        val electricityServiceTl =
            ServiceTlEntity.electricityServiceTl(ctx, electricityService.serviceId)
        // ACT
        serviceDao.update(electricityService, electricityServiceTl)
        // ASSERT
        serviceDao.findDistinctById(rentService.serviceId).test {
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
        val rentServiceTl = ServiceTlEntity.rentServiceTl(ctx, rentService.serviceId)
        serviceDao.insert(rentService, rentServiceTl)
        // ACT
        serviceDao.deleteById(rentService.serviceId)
        // ASSERT
        serviceDao.findDistinctById(rentService.serviceId).test {
            assertThat(awaitItem()).isNull()
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteAllServicesAndFindAll_returnsIsEmpty() = runTest {
        // ARRANGE
        val rentService = ServiceEntity.rentService()
        val rentServiceTl = ServiceTlEntity.rentServiceTl(ctx, rentService.serviceId)
        val electricityService = ServiceEntity.electricityService()
        val electricityServiceTl =
            ServiceTlEntity.electricityServiceTl(ctx, electricityService.serviceId)
        serviceDao.insert(rentService, rentServiceTl)
        serviceDao.insert(electricityService, electricityServiceTl)
        // ACT
        serviceDao.deleteAll()
        // ASSERT
        serviceDao.findDistinctAll().test {
            assertThat(awaitItem()).isEmpty()
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun initiateServicePosAndFindAll_return_theSeqServicesPosOrdered_inFlow() = runTest {
        // 1. ARRANGE
        val service1 = ServiceEntity.defaultService(serviceType = ServiceType.RENT)
        val serviceTl1 = ServiceTlEntity.rentServiceTl(ctx, service1.serviceId)
        val service2 = ServiceEntity.defaultService(serviceType = ServiceType.INTERNET)
        val serviceTl2 = ServiceTlEntity.rentServiceTl(ctx, service2.serviceId)
        // 1. ACT
        serviceDao.insert(service1, serviceTl1)
        serviceDao.insert(service2, serviceTl2)
        // 2. ARRANGE
        val service3 =
            ServiceEntity.defaultService(servicePos = 2, serviceType = ServiceType.GARBAGE)
        val serviceTl3 = ServiceTlEntity.rentServiceTl(ctx, service3.serviceId)
        // 2. ACT
        serviceDao.insert(service3, serviceTl3)
        // ASSERT
        serviceDao.findDistinctAll().test {
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
        val serviceTl1 = ServiceTlEntity.rentServiceTl(ctx, service1.serviceId)
        val service2 = ServiceEntity.defaultService(serviceType = ServiceType.INTERNET)
        val serviceTl2 = ServiceTlEntity.rentServiceTl(ctx, service2.serviceId)
        val service3 = ServiceEntity.defaultService(serviceType = ServiceType.GARBAGE)
        val serviceTl3 = ServiceTlEntity.rentServiceTl(ctx, service3.serviceId)
        serviceDao.insert(service1, serviceTl1)
        serviceDao.insert(service2, serviceTl2)
        serviceDao.insert(service3, serviceTl3)
        lateinit var testService: ServiceView
        serviceDao.findDistinctById(service3.serviceId).test {
            testService = awaitItem()
        }
        // ACT
        serviceDao.update(
            ServiceEntity.defaultService(
                serviceId = testService.data.serviceId,
                servicePos = 2,
                serviceType = testService.data.serviceType
            ), serviceTl3
        )
        // ASSERT
        serviceDao.findDistinctAll().test {
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
        val serviceTl1 = ServiceTlEntity.rentServiceTl(ctx, service1.serviceId)
        val service2 = ServiceEntity.defaultService(serviceType = ServiceType.RENT)
        val serviceTl2 = ServiceTlEntity.rentServiceTl(ctx, service2.serviceId)
        // ACT
        serviceDao.insert(service1, serviceTl1)
        serviceDao.insert(service2, serviceTl2)

    }

    companion object {
        suspend fun insertService(
            ctx: Context, db: HomeDatabase,
            service: ServiceEntity = ServiceEntity.defaultService()
        ): UUID {
            val serviceTl =
                when (service.serviceType) {
                    ServiceType.RENT -> ServiceTlEntity.rentServiceTl(ctx, service.serviceId)
                    ServiceType.ELECTRICITY -> ServiceTlEntity.electricityServiceTl(
                        ctx, service.serviceId
                    )
                    ServiceType.GAS -> ServiceTlEntity.gasServiceTl(ctx, service.serviceId)
                    ServiceType.COLD_WATER -> ServiceTlEntity.coldWaterServiceTl(
                        ctx, service.serviceId
                    )
                    ServiceType.WASTE -> ServiceTlEntity.wasteServiceTl(ctx, service.serviceId)
                    ServiceType.HEATING -> ServiceTlEntity.heatingServiceTl(ctx, service.serviceId)
                    ServiceType.HOT_WATER -> ServiceTlEntity.hotWaterServiceTl(
                        ctx, service.serviceId
                    )
                    ServiceType.GARBAGE -> ServiceTlEntity.garbageServiceTl(ctx, service.serviceId)
                    ServiceType.DOORPHONE -> ServiceTlEntity.doorphoneServiceTl(
                        ctx, service.serviceId
                    )
                    ServiceType.PHONE -> ServiceTlEntity.phoneServiceTl(ctx, service.serviceId)
                    ServiceType.USGO -> ServiceTlEntity.ugsoServiceTl(ctx, service.serviceId)
                    ServiceType.INTERNET -> ServiceTlEntity.internetServiceTl(
                        ctx, service.serviceId
                    )
                }
            db.serviceDao().insert(service, serviceTl)
            return service.serviceId
        }
    }
}