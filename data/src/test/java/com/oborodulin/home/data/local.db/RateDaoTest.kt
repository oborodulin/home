package com.oborodulin.home.data.local.db

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
import java.time.OffsetDateTime
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
private const val TAG = "Testing.db.RateDaoTest"

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
        // ARRANGE
        val payerId = PayerDaoTest.insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
        val electricity = ServiceEntity.electricity2Service()
        val rent = ServiceEntity.rent1Service()
        ServiceDaoTest.insertService(ctx, db, electricity)
        ServiceDaoTest.insertService(ctx, db, rent)

        val payerRentId = PayerDaoTest.insertPayerService(db, payerId, rent.serviceId)
        // ACT
        insertRate(db, electricity)
        insertRate(db, rent, payerRentId)
        // ASSERT
        rateDao.findDistinctAll().test {
            assertThat(awaitItem()).hasSize(5)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertPayerServiceRatesAndFindByPayerId_shouldReturn_thePayerRates_inFlow() = runTest {
        // ARRANGE
        val payerId = PayerDaoTest.insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
        val electricity = ServiceEntity.electricity2Service()
        ServiceDaoTest.insertService(ctx, db, electricity)
        val payerElectricityId = PayerDaoTest.insertPayerService(db, payerId, electricity.serviceId)
        // ACT
        insertRate(db, electricity, payerElectricityId)
        // ASSERT
        rateDao.findDistinctByPayerId(payerId).test {
            assertThat(awaitItem()).hasSize(4)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertServiceRatesAndFindByServiceId_shouldReturn_theServiceRates_inFlow() = runTest {
        // ARRANGE
        val electricity = ServiceEntity.electricity2Service()
        ServiceDaoTest.insertService(ctx, db, electricity)
        // ACT
        insertRate(db, electricity)
        // ASSERT
        rateDao.findDistinctByServiceId(electricity.serviceId).test {
            assertThat(awaitItem()).hasSize(4)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertPayerServiceRatesAndFindByPayerServiceId_shouldReturn_thePayerServiceRates_inFlow() =
        runTest {
            // ARRANGE
            val payerId = PayerDaoTest.insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
            val electricity = ServiceEntity.electricity2Service()
            ServiceDaoTest.insertService(ctx, db, electricity)
            val payerElectricityId =
                PayerDaoTest.insertPayerService(db, payerId, electricity.serviceId)
            // ACT
            insertRate(db, electricity, payerElectricityId)
            // ASSERT
            rateDao.findDistinctByPayerServiceId(payerElectricityId).test {
                assertThat(awaitItem()).hasSize(4)
                cancel()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertRentRatesAndPayerServiceDebtView_shouldReturn_correctPayerServiceDebt_inFlow() =
        runTest {
            // ARRANGE
            val actualPayerId = PayerDaoTest.insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
            // Service:
            val rent = ServiceEntity.rent1Service()
            val rentIds = ServiceDaoTest.insertService(ctx, db, rent)
            // Payer service:
            val payerRentId = PayerDaoTest.insertPayerService(
                db, actualPayerId, rentIds.serviceId, currentDateTime.minusMonths(2)
            )

            // ACT
            // Payer service rates:
            insertRate(db, rent, payerRentId, currentDateTime.minusMonths(3))
            insertRate(
                db, rent, payerRentId, currentDateTime.minusMonths(1),
                RateEntity.DEF_RENT_PAYER_RATE.add(BigDecimal.ONE)
            )

            // ASSERT
            rateDao.findSubtotalDebtsByPayerId(actualPayerId).test {
                val subtotals = awaitItem()
                subtotals.forEach {
                    println("subtotals: '%s' = %s".format(it.serviceType, it.serviceDebt))
                }
                assertThat(subtotals).hasSize(9)
                cancel()
            }
        }

    /*
        @OptIn(ExperimentalCoroutinesApi::class)
        @Test
        fun insertRatesAndFindRatesByPayerServices_shouldReturn_firstPrivilegesRates_inFlow() =
            runTest {
                // ARRANGE
                // Payers:
                val payer1Id = PayerDaoTest.insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))

                // Services:
                // entities:
                val rent = ServiceEntity.rent1Service()
                val electricity = ServiceEntity.electricity2Service()
                val gas = ServiceEntity.gas3Service()
                val coldWater = ServiceEntity.coldWater4Service()
                val waste = ServiceEntity.waste5Service()
                val heating = ServiceEntity.heating6Service()
                val hotWater = ServiceEntity.hotWater7Service()
                val garbage = ServiceEntity.garbage8Service()
                val doorphone = ServiceEntity.doorphone9Service()
                val phone = ServiceEntity.phone10Service()
                val ugso = ServiceEntity.ugso11Service()
                val internet = ServiceEntity.internet12Service()
                // inserted ids:
                val rentIds = ServiceDaoTest.insertService(ctx, db, rent)
                val electricityIds = ServiceDaoTest.insertService(ctx, db, electricity)
                val gasIds = ServiceDaoTest.insertService(ctx, db, gas)
                val coldWaterIds = ServiceDaoTest.insertService(ctx, db, coldWater)
                val wasteIds = ServiceDaoTest.insertService(ctx, db, waste)
                val heatingIds = ServiceDaoTest.insertService(ctx, db, heating)
                val hotWaterIds = ServiceDaoTest.insertService(ctx, db, hotWater)
                val garbageIds = ServiceDaoTest.insertService(ctx, db, garbage)
                val doorphoneIds = ServiceDaoTest.insertService(ctx, db, doorphone)
                val phoneIds = ServiceDaoTest.insertService(ctx, db, phone)
                val ugsoIds = ServiceDaoTest.insertService(ctx, db, ugso)
                val internetIds = ServiceDaoTest.insertService(ctx, db, internet)

                // Service rates:
                insertRate(db, electricity)
                insertRate(db, gas)
                insertRate(db, coldWater)
                insertRate(db, waste)
                insertRate(db, heating)
                insertRate(db, hotWater)

                // Payer 1 services:
                val payer1RentId = PayerDaoTest.insertPayerService(db, payer1Id, rentIds.serviceId)
                val payer1ElectricityId =
                    PayerDaoTest.insertPayerService(
                        db, payer1Id, electricityIds.serviceId, true, isAllocateRate = true
                    )
                val payer1GasId = PayerDaoTest.insertPayerService(db, payer1Id, gasIds.serviceId, true)
                val payer1ColdWaterId =
                    PayerDaoTest.insertPayerService(db, payer1Id, coldWaterIds.serviceId, true)
                val payer1HeatingId =
                    PayerDaoTest.insertPayerService(db, payer1Id, heatingIds.serviceId, true)
                val payer1WasteId = PayerDaoTest.insertPayerService(db, payer1Id, wasteIds.serviceId)
                val payer1HotWaterId =
                    PayerDaoTest.insertPayerService(db, payer1Id, hotWaterIds.serviceId, true)
                val payer1GarbageId =
                    PayerDaoTest.insertPayerService(db, payer1Id, garbageIds.serviceId)
                val payer1DoorphoneId =
                    PayerDaoTest.insertPayerService(db, payer1Id, doorphoneIds.serviceId)
                val payer1PhoneId = PayerDaoTest.insertPayerService(db, payer1Id, phoneIds.serviceId)
                val payer1UgsoId = PayerDaoTest.insertPayerService(db, payer1Id, ugsoIds.serviceId)
                val payer1InternetId =
                    PayerDaoTest.insertPayerService(db, payer1Id, internetIds.serviceId)
                // Meters:
                val electricityMeter = MeterEntity.electricityMeter(ctx, payer1Id)
                MeterDaoTest.insertMeter(ctx, db, electricityMeter)
                val gasMeter = MeterEntity.gasMeter(ctx, payer1Id, currentDateTime)
                MeterDaoTest.insertMeter(ctx, db, gasMeter)
                val coldWaterMeter = MeterEntity.coldWaterMeter(ctx, payer1Id, currentDateTime)
                MeterDaoTest.insertMeter(ctx, db, coldWaterMeter)
                val heatingMeter = MeterEntity.heatingMeter(ctx, payer1Id, currentDateTime)
                MeterDaoTest.insertMeter(ctx, db, heatingMeter)
                val hotWaterMeter = MeterEntity.hotWaterMeter(ctx, payer1Id, currentDateTime)
                MeterDaoTest.insertMeter(ctx, db, hotWaterMeter)
                // Meter values:
                MeterDaoTest.insertMeterValues(db, electricityMeter, currentDateTime)
                MeterDaoTest.insertMeterValues(db, gasMeter, currentDateTime)
                MeterDaoTest.insertMeterValues(db, coldWaterMeter, currentDateTime)
                MeterDaoTest.insertMeterValues(db, heatingMeter, currentDateTime)

                // ACT
                // Payer service rates:
                insertRate(db, rent, payer1RentId)
                insertRate(db, garbage, payer1GarbageId, true)
                insertRate(db, doorphone, payer1DoorphoneId)
                insertRate(db, phone, payer1PhoneId)
                insertRate(db, internet, payer1InternetId)

                // ASSERT
                rateDao.findSubtotalDebtsByPayerId(payer1Id).test {
                    val subtotals = awaitItem()
                    subtotals.forEach {
                        println("subtotals: '%s' = %s".format(it.serviceType, it.serviceDebt))
                    }
                    assertThat(subtotals).hasSize(9)
                    cancel()
                }


            }

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
        suspend fun insertRate(
            db: HomeDatabase,
            service: ServiceEntity,
            payerServiceId: UUID? = null,
            startDate: OffsetDateTime = OffsetDateTime.now(),
            rateValue: BigDecimal = BigDecimal.ZERO,
            isPerPerson: Boolean = false,
            isPrivileges: Boolean = false
        ): UUID {
            val rates: MutableList<RateEntity> = mutableListOf()
            var rate: RateEntity = RateEntity.defaultRate()
            when (service.serviceType) {
                // Service rates:
                ServiceType.ELECTRICITY -> {
                    rates.add(
                        RateEntity.electricityRateFrom0To150(
                            service.serviceId, payerServiceId, startDate, rateValue
                        )
                    )
                    rates.add(
                        RateEntity.electricityRateFrom150To800(
                            service.serviceId, payerServiceId, startDate, rateValue
                        )
                    )
                    rates.add(
                        RateEntity.electricityRateFrom800(
                            service.serviceId, payerServiceId, startDate, rateValue
                        )
                    )
                    rate = RateEntity.electricityPrivilegesRate(
                        service.serviceId, payerServiceId, startDate, rateValue
                    )
                }
                ServiceType.GAS -> rate =
                    RateEntity.gasRate(service.serviceId, startDate, true, isPrivileges, rateValue)
                ServiceType.COLD_WATER ->
                    rate = RateEntity.coldWaterRate(
                        service.serviceId, startDate, isPerPerson, isPrivileges, rateValue
                    )
                ServiceType.WASTE ->
                    rate = RateEntity.wasteRate(
                        service.serviceId, startDate, isPerPerson, isPrivileges, rateValue
                    )
                ServiceType.HEATING ->
                    rate = RateEntity.heatingRate(
                        service.serviceId, startDate, isPerPerson, isPrivileges, rateValue
                    )
                ServiceType.HOT_WATER ->
                    rate = RateEntity.hotWaterRate(
                        service.serviceId, startDate, isPerPerson, isPrivileges, rateValue
                    )
                // Payer service rates:
                ServiceType.RENT ->
                    payerServiceId?.let {
                        rate = RateEntity.rentRateForPayer(
                            service.serviceId, it, startDate, isPerPerson, isPrivileges, rateValue
                        )
                    }
                ServiceType.GARBAGE ->
                    payerServiceId?.let {
                        rate = RateEntity.garbageRateForPayer(
                            service.serviceId, it, startDate, isPerPerson, isPrivileges, rateValue
                        )
                    }
                ServiceType.DOORPHONE ->
                    payerServiceId?.let {
                        rate = RateEntity.doorphoneRateForPayer(
                            service.serviceId, it, startDate, isPerPerson, isPrivileges, rateValue
                        )
                    }
                ServiceType.PHONE ->
                    payerServiceId?.let {
                        rate = RateEntity.phoneRateForPayer(
                            service.serviceId, it, startDate, isPerPerson, isPrivileges, rateValue
                        )
                    }
                ServiceType.INTERNET ->
                    payerServiceId?.let {
                        rate = RateEntity.internetRateForPayer(
                            service.serviceId, it, startDate, isPerPerson, isPrivileges, rateValue
                        )
                    }
                ServiceType.USGO -> {}
            }
            if (rates.isNotEmpty()) db.rateDao().insert(rates)
            db.rateDao().insert(rate)
            return rate.rateId
        }
    }
}