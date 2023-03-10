package com.oborodulin.home.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.os.Build
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.dao.RateDao
import com.oborodulin.home.data.local.db.dao.ServiceDao
import com.oborodulin.home.data.local.db.entities.PayerEntity
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
import java.math.BigDecimal
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

    data class PayerServiceIds(val payerId: UUID, val payerServiceId: UUID)

    @Before
    override fun setUp() {
        super.setUp()
        rateDao = rateDao()
        payerDao = payerDao()
        serviceDao = serviceDao()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertRatesAndFindAll_shouldReturn_allRates_inFlow() = runTest {
        // 1. ARRANGE & ACT
        insertElectricityServiceRates(ctx, db)
        // 2. ARRANGE & ACT
        insertPayerRentServiceRates(ctx, db)
        // ASSERT
        rateDao.findDistinctAll().test {
            assertThat(awaitItem()).hasSize(6)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertServiceRatesAndFindByServiceId_shouldReturn_theServiceRates_inFlow() = runTest {
        // ARRANGE & ACT
        val electricityServiceId = insertElectricityServiceRates(ctx, db)
        // ASSERT
        rateDao.findDistinctByServiceId(electricityServiceId).test {
            assertThat(awaitItem()).hasSize(3)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertPayerRatesAndFindByPayerId_shouldReturn_theRates_inFlow() = runTest {
        // ARRANGE & ACT
        val ids = insertPayerRentServiceRates(ctx, db)
        // ASSERT
        rateDao.findDistinctByPayerId(ids.payerId).test {
            assertThat(awaitItem()).hasSize(3)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertPayerRatesAndFindByPayerServiceId_shouldReturn_theRates_inFlow() = runTest {
        // ARRANGE & ACT
        val ids = insertPayerRentServiceRates(ctx, db)
        // ASSERT
        rateDao.findDistinctByPayerServiceId(ids.payerServiceId).test {
            assertThat(awaitItem()).hasSize(3)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertRatesAndFindRatesByPayerServices_shouldReturn_firstPrivilegesRates_inFlow() =
        runTest {
            // ARRANGE
            // Payers:
            val payer1Id = PayerDaoTest.insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
            val payer2Id = PayerDaoTest.insertPayer(db, PayerEntity.favoritePayer(ctx))

            // Services:
            val rentId = ServiceDaoTest.insertService(ctx, db, ServiceEntity.rentService())
            val electricityId =
                ServiceDaoTest.insertService(ctx, db, ServiceEntity.electricityService())
            val gasId = ServiceDaoTest.insertService(ctx, db, ServiceEntity.gasService())
            val coldWaterId =
                ServiceDaoTest.insertService(ctx, db, ServiceEntity.coldWaterService())
            val heatingId = ServiceDaoTest.insertService(ctx, db, ServiceEntity.heatingService())
            val wasteId = ServiceDaoTest.insertService(ctx, db, ServiceEntity.wasteService())
            val hotWaterId = ServiceDaoTest.insertService(ctx, db, ServiceEntity.hotWaterService())
            val garbageId = ServiceDaoTest.insertService(ctx, db, ServiceEntity.garbageService())
            val doorphoneId =
                ServiceDaoTest.insertService(ctx, db, ServiceEntity.doorphoneService())
            val phoneId = ServiceDaoTest.insertService(ctx, db, ServiceEntity.phoneService())
            val ugsoId = ServiceDaoTest.insertService(ctx, db, ServiceEntity.ugsoService())
            val internetId = ServiceDaoTest.insertService(ctx, db, ServiceEntity.internetService())

            // Service rates:
            // electricity
            rateDao.insert(RateEntity.electricityRateFrom0To150(electricityId))
            rateDao.insert(RateEntity.electricityRateFrom150To800(electricityId))
            rateDao.insert(RateEntity.electricityRateFrom800(electricityId))
            rateDao.insert(RateEntity.electricityPrivilegesRate(electricityId))
            // gas
            rateDao.insert(RateEntity.gasRate(gasId))
            // cold water
            rateDao.insert(RateEntity.coldWaterRate(coldWaterId))
            // heating
            rateDao.insert(RateEntity.heatingRate(heatingId))
            // waste
            rateDao.insert(RateEntity.wasteRate(wasteId))
            // hot water
            rateDao.insert(RateEntity.hotWaterRate(hotWaterId))

            // Payer 1 services:
            val payer1RentId = PayerDaoTest.insertPayerService(db, payer1Id, rentId)
            val payer1ElectricityId = PayerDaoTest.insertPayerService(db, payer1Id, electricityId)
            val payer1GasId = PayerDaoTest.insertPayerService(db, payer1Id, gasId)
            val payer1ColdWaterId = PayerDaoTest.insertPayerService(db, payer1Id, coldWaterId)
            val payer1HeatingId = PayerDaoTest.insertPayerService(db, payer1Id, heatingId)
            val payer1WasteId = PayerDaoTest.insertPayerService(db, payer1Id, wasteId)
            val payer1HotWaterId = PayerDaoTest.insertPayerService(db, payer1Id, hotWaterId)
            val payer1GarbageId = PayerDaoTest.insertPayerService(db, payer1Id, garbageId)
            val payer1DoorphoneId = PayerDaoTest.insertPayerService(db, payer1Id, doorphoneId)
            val payer1PhoneId = PayerDaoTest.insertPayerService(db, payer1Id, phoneId)
            val payer1UgsoId = PayerDaoTest.insertPayerService(db, payer1Id, ugsoId)
            val payer1InternetId = PayerDaoTest.insertPayerService(db, payer1Id, internetId)

            // ACT

            // ASSERT
/*            rateDao.findRatesByPayerServices().test {
                val services = awaitItem()
                assertThat(services).hasSize(2)
                cancel()
            }

 */
        }
/*
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateServiceAndFindById_shouldReturn_theUpdatedService_inFlow() = runTest {
        // ARRANGE
        val rentService = ServiceEntity.rentService()
        val rentServiceTl = ServiceTlEntity.rentServiceTl(ctx, rentService.serviceId)
        rateDao.insert(rentService, rentServiceTl)
        val electricityService = ServiceEntity.electricityService(rentService.serviceId)
        val electricityServiceTl =
            ServiceTlEntity.electricityServiceTl(ctx, electricityService.serviceId)
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
        val rentServiceTl = ServiceTlEntity.rentServiceTl(ctx, rentService.serviceId)
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
        val rentServiceTl = ServiceTlEntity.rentServiceTl(ctx, rentService.serviceId)
        val electricityService = ServiceEntity.electricityService()
        val electricityServiceTl =
            ServiceTlEntity.electricityServiceTl(ctx, electricityService.serviceId)
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
        val serviceTl1 = ServiceTlEntity.rentServiceTl(ctx, service1.serviceId)
        val service2 = ServiceEntity.defaultService(serviceType = ServiceType.INTERNET)
        val serviceTl2 = ServiceTlEntity.rentServiceTl(ctx, service2.serviceId)
        // 1. ACT
        rateDao.insert(service1, serviceTl1)
        rateDao.insert(service2, serviceTl2)
        // 2. ARRANGE
        val service3 =
            ServiceEntity.defaultService(servicePos = 2, serviceType = ServiceType.GARBAGE)
        val serviceTl3 = ServiceTlEntity.rentServiceTl(ctx, service3.serviceId)
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
        val serviceTl1 = ServiceTlEntity.rentServiceTl(ctx, service1.serviceId)
        val service2 = ServiceEntity.defaultService(serviceType = ServiceType.INTERNET)
        val serviceTl2 = ServiceTlEntity.rentServiceTl(ctx, service2.serviceId)
        val service3 = ServiceEntity.defaultService(serviceType = ServiceType.GARBAGE)
        val serviceTl3 = ServiceTlEntity.rentServiceTl(ctx, service3.serviceId)
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
        val serviceTl1 = ServiceTlEntity.rentServiceTl(ctx, service1.serviceId)
        val service2 = ServiceEntity.defaultService(serviceType = ServiceType.RENT)
        val serviceTl2 = ServiceTlEntity.rentServiceTl(ctx, service2.serviceId)
        // ACT
        rateDao.insert(service1, serviceTl1)
        rateDao.insert(service2, serviceTl2)

    }
*/
    companion object {
        suspend fun insertElectricityServiceRates(ctx: Context, db: HomeDatabase): UUID {
            val electricityServiceId =
                ServiceDaoTest.insertService(
                    ctx, db, ServiceEntity.electricityService()
                )
            db.rateDao().insert(
                listOf(
                    RateEntity.electricityRateFrom0To150(electricityServiceId),
                    RateEntity.electricityRateFrom150To800(electricityServiceId),
                    RateEntity.electricityRateFrom800(electricityServiceId)
                )
            )
            return electricityServiceId
        }

        suspend fun insertPayerRentServiceRates(ctx: Context, db: HomeDatabase): PayerServiceIds {
            val payer = PayerEntity.payerWithTwoPersons(ctx)
            val rentService = ServiceEntity.rentService()
            val payerRentServiceId = PayerDaoTest.insertPayerService(
                db = db, ctx = ctx, payer = payer, service = rentService
            )
            db.rateDao()
                .insert(RateEntity.rentRateForPayer(rentService.serviceId, payerRentServiceId!!))
            db.rateDao().insert(
                RateEntity.perPersonRate(
                    serviceId = rentService.serviceId,
                    payerServiceId = payerRentServiceId,
                    rateValue = BigDecimal.valueOf(17.74)
                )
            )
            db.rateDao().insert(
                RateEntity.privilegesRate(
                    serviceId = rentService.serviceId,
                    payerServiceId = payerRentServiceId,
                    rateValue = BigDecimal.valueOf(23.53)
                )
            )
            return PayerServiceIds(payerId = payer.payerId, payerServiceId = payerRentServiceId)
        }
    }
}