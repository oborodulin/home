package com.oborodulin.home.data.local.db

import android.content.Context
import android.os.Build
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.oborodulin.home.common.util.Constants
import com.oborodulin.home.data.local.db.converters.DateTypeConverter
import com.oborodulin.home.data.local.db.dao.MeterDao
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.entities.*
import com.oborodulin.home.data.util.MeterType
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import timber.log.Timber
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
private const val TAG = "Testing.local.db"

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
@MediumTest
class MeterDaoTest : HomeDatabaseTest() {
    private lateinit var meterDao: MeterDao
    private lateinit var payerDao: PayerDao

    data class MeterIds(val meterId: UUID, val meterTlId: UUID)

    @Before
    override fun setUp() {
        super.setUp()
        meterDao = meterDao()
        payerDao = payerDao()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertMetersAndFindAll_shouldReturn_theMetersList_inFlow() = runTest {
        // ARRANGE
        val actualPayerId = PayerDaoTest.insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
        val electricityMeter = MeterEntity.electricityMeter(ctx, actualPayerId)
        val gasMeter = MeterEntity.gasMeter(ctx, actualPayerId)
        // ACT
        meterDao.insert(
            electricityMeter, MeterTlEntity.electricityMeterTl(ctx, electricityMeter.meterId)
        )
        meterDao.insert(gasMeter, MeterTlEntity.gasMeterTl(ctx, gasMeter.meterId))
        // ASSERT
        meterDao.findDistinctAll().test {
            assertThat(awaitItem()).hasSize(2)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateMeterAndFindById_shouldReturn_theUpdatedMeter_inFlow() = runTest {
        // ARRANGE
        val actualPayerId = PayerDaoTest.insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
        val actualMeterIds = insertMeter(ctx, db, MeterEntity.electricityMeter(ctx, actualPayerId))
        val updatedMeter = MeterEntity.defaultMeter(
            payerId = actualPayerId, meterId = actualMeterIds.meterId,
            meterType = MeterType.COLD_WATER,
            maxValue = BigDecimal.valueOf(9999.999)
        )
        val updatedMeterTl = MeterTlEntity.defaultMeterTl(
            meterTlId = actualMeterIds.meterTlId, meterId = actualMeterIds.meterId, measureUnit = ""
        )
        // ACT
        meterDao.update(updatedMeter, updatedMeterTl)
        // ASSERT
        meterDao.findDistinctById(actualMeterIds.meterId).test {
            val meter = awaitItem()
            assertThat(meter).isNotNull()
            assertThat(meter.data).isEqualTo(updatedMeter)
            assertThat(meter.tl).isEqualTo(updatedMeterTl)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertMetersAndFindByPayerId_shouldReturn_theMeter_inFlow() = runTest {
        // ARRANGE
        val twoPersonsPayerId = PayerDaoTest.insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
        // ACT
        insertMeter(ctx, db, MeterEntity.electricityMeter(ctx, twoPersonsPayerId))
        insertMeter(ctx, db, MeterEntity.hotWaterMeter(ctx, twoPersonsPayerId))
        // ASSERT
        meterDao.findDistinctByPayerId(twoPersonsPayerId).test {
            assertThat(awaitItem()).hasSize(2)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertMeterValuesAndFindPrevMetersValuesByPayerId_shouldReturn_theCorrectPrevMetersValues_inFlow() =
        runTest {
            // ARRANGE
            val currentDateTime: OffsetDateTime = OffsetDateTime.now()
            // Payer:
            val actualPayerId = PayerDaoTest.insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
            // Services:
            val electricityIds =
                ServiceDaoTest.insertService(ctx, db, ServiceEntity.electricity2Service())
            val gasIds = ServiceDaoTest.insertService(ctx, db, ServiceEntity.gas3Service())
            val wasteIds = ServiceDaoTest.insertService(ctx, db, ServiceEntity.waste5Service())
            val hotWaterIds =
                ServiceDaoTest.insertService(ctx, db, ServiceEntity.hotWater7Service())
            // Payer services:
            val payerElectricityId =
                PayerDaoTest.insertPayerService(db, actualPayerId, electricityIds.serviceId, true)
            val payerGasId =
                PayerDaoTest.insertPayerService(db, actualPayerId, gasIds.serviceId, true)
            val payerWasteId =
                PayerDaoTest.insertPayerService(db, actualPayerId, wasteIds.serviceId)
            val payerHotWaterId =
                PayerDaoTest.insertPayerService(db, actualPayerId, hotWaterIds.serviceId, true)

            // Meters:
            val electricityMeterIds =
                insertMeter(ctx, db, MeterEntity.electricityMeter(ctx, actualPayerId))
            val gasMeterIds =
                insertMeter(
                    ctx, db,
                    MeterEntity.gasMeter(
                        ctx, actualPayerId,
                        passportDate = currentDateTime.minusMonths(2).withDayOfMonth(1)
                    )
                )
            val hotWaterMeterIds =
                insertMeter(
                    ctx, db, MeterEntity.hotWaterMeter(
                        ctx, actualPayerId,
                        passportDate = currentDateTime.minusMonths(1).withDayOfMonth(1)
                    )
                )

            // ACT
            meterDao.insert(
                // Electricity
                MeterValueEntity.defaultMeterValue(
                    meterId = electricityMeterIds.meterId,
                    valueDate = currentDateTime.minusMonths(2).withDayOfMonth(1),
                    meterValue = BigDecimal.valueOf(9558)
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = electricityMeterIds.meterId,
                    valueDate = currentDateTime.minusMonths(1).withDayOfMonth(1),
                    meterValue = BigDecimal.valueOf(9642)
                ),
                // Gas
                /*MeterValueEntity.defaultMeterValue(
                    meterId = gasMeterIds.meterId,
                    valueDate = currentDateTime.minusMonths(1).withDayOfMonth(1),
                    meterValue = BigDecimal.valueOf(1.954)
                ),*/
                MeterValueEntity.defaultMeterValue(
                    meterId = gasMeterIds.meterId,
                    valueDate = currentDateTime.withDayOfMonth(1),
                    meterValue = BigDecimal.valueOf(2.751)
                ),
                // Hot Water
                MeterValueEntity.defaultMeterValue(
                    meterId = hotWaterMeterIds.meterId,
                    valueDate = currentDateTime.minusMonths(1).withDayOfMonth(1),
                    meterValue = BigDecimal.valueOf(2150.124)
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = hotWaterMeterIds.meterId,
                    valueDate = currentDateTime.withDayOfMonth(1),
                    meterValue = BigDecimal.valueOf(2154.987)
                )
            )
            // ASSERT
            meterDao.findDistinctPrevMetersValuesByPayerId(actualPayerId).test {
                val prevMeterValues = awaitItem()
                prevMeterValues.forEach {
                    Timber.tag(TAG).d("prevMeterValue: %s", it)
                }
                assertThat(prevMeterValues).isNotEmpty()
                assertThat(prevMeterValues[0].serviceId).isEqualTo(electricityIds.serviceId)
                assertThat(prevMeterValues[0].prevValue).isEqualTo(BigDecimal.valueOf(9642))
                assertThat(prevMeterValues[0].prevLastDate).isEqualTo(
                    currentDateTime.minusMonths(1).withDayOfMonth(1)
                )
                assertThat(prevMeterValues[0].currentValue).isNull()

                assertThat(prevMeterValues[1].serviceId).isEqualTo(gasIds.serviceId)
                assertThat(prevMeterValues[1].prevValue).isEqualTo(MeterEntity.DEF_GAS_INIT_VAL)
                assertThat(prevMeterValues[1].prevLastDate).isEqualTo(
                    currentDateTime.minusMonths(1).withDayOfMonth(1).format(
                        DateTimeFormatter.ofPattern(Constants.APP_FRACT_SEC_TIME)
                    )
                )
                assertThat(prevMeterValues[1].currentValue).isEqualTo(BigDecimal.valueOf(2154.987))

                assertThat(prevMeterValues[2].serviceId).isEqualTo(hotWaterIds.serviceId)
                assertThat(prevMeterValues[2].prevValue).isEqualTo(BigDecimal.valueOf(2150.124))
                assertThat(prevMeterValues[2].prevLastDate).isEqualTo(
                    currentDateTime.minusMonths(1).withDayOfMonth(1).format(
                        DateTimeFormatter.ofPattern(Constants.APP_FRACT_SEC_TIME)
                    )
                )
                assertThat(prevMeterValues[2].currentValue).isEqualTo(BigDecimal.valueOf(2154.987))
                cancel()
            }
        }

    /*
        @OptIn(ExperimentalCoroutinesApi::class)
        @Test
        fun updatePayerAndFindById_shouldReturn_theUpdatedPayer_inFlow() = runTest {
            // ARRANGE
            val actualPayerId = insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
            val expectedPayer = PayerEntity.favoritePayer(ctx, actualPayerId)
            // ACT
            meterDao.update(expectedPayer)
            // ASSERT
            meterDao.findDistinctById(actualPayerId).test {
                assertThat(awaitItem()).isEqualTo(expectedPayer)
                cancel()
            }
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @Test
        fun updatePayersAndFindAll_shouldReturn_theUpdatedPayers_inFlow() = runTest {
            // 1. ARRANGE
            val twoPersonsPayer = PayerEntity.payerWithTwoPersons(ctx)
            val favoritePayer = PayerEntity.favoritePayer(ctx)
            // 1. ACT
            meterDao.insert(twoPersonsPayer, favoritePayer)
            // 1. ASSERT
            meterDao.findDistinctAll().test {
                assertThat(awaitItem()).containsExactly(favoritePayer, twoPersonsPayer)
                cancel()
            }

            // 2. ARRANGE
            val expectedByPaymentDayPayer =
                PayerEntity.payerWithAlignByPaymentDay(ctx, favoritePayer.payerId)
            val expectedFavoritePayer =
                PayerEntity.favoritePayer(ctx, twoPersonsPayer.payerId)

            // 2. ACT
            meterDao.update(expectedByPaymentDayPayer, expectedFavoritePayer)
            // 2. ASSERT
            meterDao.findDistinctById(favoritePayer.payerId).test {
                assertThat(awaitItem()).isEqualTo(expectedByPaymentDayPayer)
                cancel()
            }
            meterDao.findDistinctById(twoPersonsPayer.payerId).test {
                assertThat(awaitItem()).isEqualTo(expectedFavoritePayer)
                cancel()
            }
        }

        @Test
        fun deleteInsertedPayerAndFindById_shouldReturn_IsNull() = runBlocking {
            // ARRANGE
            val actualPayer = PayerEntity.payerWithTwoPersons(ctx)
            // 1. ACT
            meterDao.insert(actualPayer)
            // 1. ASSERT
            val latch1 = CountDownLatch(1)
            val job1 = async(Dispatchers.IO) {
                meterDao.findDistinctById(actualPayer.payerId).collect {
                    assertThat(it).isEqualTo(actualPayer)
                    latch1.countDown()
                }
            }
            latch1.await()
            job1.cancelAndJoin()
            // 2. ACT
            meterDao.delete(actualPayer)
            // 2. ASSERT
            val latch2 = CountDownLatch(1)
            val job2 = async(Dispatchers.IO) {
                meterDao.findDistinctById(actualPayer.payerId).collect {
                    assertThat(it).isNull()
                    latch2.countDown()
                }
            }
            latch2.await()
            job2.cancelAndJoin()
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @Test
        fun deletePayerByIdAndFindById_shouldReturn_IsNull() = runTest {
            // ARRANGE
            val actualPayerId = insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
            // ACT
            meterDao.deleteById(actualPayerId)
            // ASSERT
            meterDao.findDistinctById(actualPayerId).test {
                assertThat(awaitItem()).isNull()
                cancel()
            }
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @Test
        fun deleteInsertedPayersListAndFindAll_shouldReturn_IsEmptyList() = runTest {
            // ARRANGE
            val actualTwoPersonsPayer = PayerEntity.payerWithTwoPersons(ctx)
            val actualFavoritePayer = PayerEntity.favoritePayer(ctx)
            meterDao.insert(actualTwoPersonsPayer, actualFavoritePayer)
            // ACT
            meterDao.delete(listOf(actualTwoPersonsPayer, actualFavoritePayer))
            // ASSERT
            meterDao.findDistinctAll().test {
                assertThat(awaitItem()).isEmpty()
                cancel()
            }
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @Test
        fun deleteAllInsertedPayersAndFindAll_shouldReturn_IsEmptyList() = runTest {
            // ARRANGE
            val actualTwoPersonsPayer = PayerEntity.payerWithTwoPersons(ctx)
            val actualFavoritePayer = PayerEntity.favoritePayer(ctx)
            // 1. ACT
            meterDao.insert(actualTwoPersonsPayer, actualFavoritePayer)
            // 1. ASSERT
            meterDao.findDistinctAll().test {
                assertThat(awaitItem()).containsExactly(actualFavoritePayer, actualTwoPersonsPayer)
                cancel()
            }
            // 2. ACT
            meterDao.deleteAll()
            // 2. ASSERT
            meterDao.findDistinctAll().test {
                assertThat(awaitItem()).isEmpty()
                cancel()
            }
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @Test
        fun findFavorite_return_theFavoritePayer_inFlow() = runTest {
            // ARRANGE
            val twoPersonsPayer = PayerEntity.payerWithTwoPersons(ctx)
            val expectedFavoritePayer = PayerEntity.favoritePayer(ctx)
            meterDao.insert(twoPersonsPayer, expectedFavoritePayer)
            // ACT & ASSERT
            meterDao.findDistinctFavorite().test {
                assertThat(awaitItem()).isEqualTo(expectedFavoritePayer)
                cancel()
            }
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @Test
        fun favoriteById_return_theChangedFavoritePayer_inFlow() = runTest {
            // ARRANGE
            val expectedTwoPersonsPayer = PayerEntity.payerWithTwoPersons(ctx)
            val favoritePayer = PayerEntity.favoritePayer(ctx)
            meterDao.insert(expectedTwoPersonsPayer, favoritePayer)
            // ACT
            meterDao.setFavoriteById(expectedTwoPersonsPayer.payerId)
            // ASSERT
            meterDao.findDistinctFavorite().test {
                assertThat(awaitItem()).isEqualTo(expectedTwoPersonsPayer)
                cancel()
            }
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @Test(expected = SQLiteConstraintException::class)
        fun duplicatePayerErcCode_ExceptionThrown() = runTest {
            // ARRANGE
            val twoPersonsPayer = PayerEntity.payerWithTwoPersons(ctx)
            val anotherTwoPersonsPayer = PayerEntity.payerWithTwoPersons(ctx)
            // ACT
            meterDao.insert(twoPersonsPayer, anotherTwoPersonsPayer)
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @Test
        fun insertPayerServicesAndFindPayersWithServices_returnsPayersWithServices_inFlow() = runTest {
            // ARRANGE
            val actualPayerId = insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
            val rentService = ServiceEntity.rentService()
            val rentServiceTl = ServiceTlEntity.rentServiceTl(ctx, rentService.serviceId)
            payerDao.insert(rentService, rentServiceTl)
            val electricityService = ServiceEntity.electricityService()
            val electricityServiceTl =
                ServiceTlEntity.electricityServiceTl(ctx, electricityService.serviceId)
            payerDao.insert(electricityService, electricityServiceTl)
            // ACT
            meterDao.insert(
                PayerServiceCrossRefEntity.populatePrivilegesPayerService(
                    payerId = actualPayerId, serviceId = rentService.serviceId
                ),
                PayerServiceCrossRefEntity.populateAllocateRatePayerService(
                    payerId = actualPayerId, serviceId = electricityService.serviceId
                )
            )
            // ASSERT
            meterDao.findPayersWithServices().test {
                val payerServices = awaitItem()
                assertThat(payerServices).isNotEmpty()
                assertThat(payerServices[0].payer.payerId).isEqualTo(actualPayerId)
                assertThat(payerServices[0].services).hasSize(2)
                assertThat(payerServices[0].services).containsAtLeast(rentService, electricityService)
                cancel()
            }
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @Test
        fun deletePayerServiceAndFindPayersWithServices_returnsPayerWithService_inFlow() = runTest {
            // ARRANGE
            val actualPayerId = insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
            val rentService = ServiceEntity.rentService()
            val rentServiceTl = ServiceTlEntity.rentServiceTl(ctx, rentService.serviceId)
            payerDao.insert(rentService, rentServiceTl)
            val electricityServiceId =
                ServiceDaoTest.insertService(ctx, db, ServiceEntity.electricityService())
            val payerRentService =
                PayerServiceCrossRefEntity.defaultPayerService(actualPayerId, rentService.serviceId)
            val payerElectricityService = PayerServiceCrossRefEntity.defaultPayerService(
                actualPayerId, electricityServiceId
            )
            meterDao.insert(payerRentService, payerElectricityService)
            // ACT
            meterDao.deleteServiceById(payerElectricityService.payerServiceId)
            // ASSERT
            meterDao.findPayersWithServices().test {
                val payerServices = awaitItem()
                assertThat(payerServices).isNotEmpty()
                assertThat(payerServices[0].payer.payerId).isEqualTo(actualPayerId)
                assertThat(payerServices[0].services).hasSize(1)
                assertThat(payerServices[0].services).containsExactly(rentService)
                cancel()
            }
        }

        @Test
        fun useAppContext() {
            // Context of the app under test.
            assertEquals("com.oborodulin.home.data.test", this.ctx.packageName)
        }
    */
    companion object {
        suspend fun insertMeter(
            ctx: Context, db: HomeDatabase,
            meter: MeterEntity = MeterEntity.defaultMeter()
        ): MeterIds {
            val meterTl =
                when (meter.meterType) {
                    MeterType.ELECTRICITY -> MeterTlEntity.electricityMeterTl(ctx, meter.meterId)
                    MeterType.GAS -> MeterTlEntity.gasMeterTl(ctx, meter.meterId)
                    MeterType.COLD_WATER -> MeterTlEntity.coldWaterMeterTl(ctx, meter.meterId)
                    MeterType.HEATING -> MeterTlEntity.heatingMeterTl(ctx, meter.meterId)
                    MeterType.HOT_WATER -> MeterTlEntity.hotWaterMeterTl(ctx, meter.meterId)
                    MeterType.NONE -> MeterTlEntity.defaultMeterTl(
                        meterId = UUID.randomUUID(), measureUnit = ""
                    )
                }
            db.meterDao().insert(meter, meterTl)
            return MeterIds(meterId = meter.meterId, meterTlId = meterTl.meterTlId)
        }
    }
}