package com.oborodulin.home.data.local.db

import android.os.Build
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.oborodulin.home.common.util.Constants
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.dao.RateDao
import com.oborodulin.home.data.local.db.dao.ReceiptDao
import com.oborodulin.home.data.local.db.dao.ServiceDao
import com.oborodulin.home.data.local.db.entities.*
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
import java.math.RoundingMode
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

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
    private lateinit var receiptDao: ReceiptDao

    @Before
    override fun setUp() {
        super.setUp()
        rateDao = rateDao()
        payerDao = payerDao()
        serviceDao = serviceDao()
        receiptDao = receiptDao()
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
    fun insertRentRatesAndFindSubtotalDebtsByPayerId_shouldReturn_correctPayerServiceDebt_inFlow() =
        runTest {
            // ARRANGE
            val rate1 = RateEntity.DEF_RENT_PAYER_RATE
            val rate2 = RateEntity.DEF_RENT_PAYER_RATE.add(BigDecimal.ONE)
            val fromServiceDate = currentDateTime.minusMonths(6)
            val rate1StartDate = currentDateTime.minusMonths(8)
            val rate2StartDate = currentDateTime.minusMonths(2)

            val expectedRate1Months = ChronoUnit.MONTHS.between(fromServiceDate, rate2StartDate)
            val expectedRate2Months = ChronoUnit.MONTHS.between(rate2StartDate, currentDateTime)

            val payer = PayerEntity.payerWithTwoPersons(ctx)
            val payerId = PayerDaoTest.insertPayer(db, payer)

            val expectedServiceDebt = rate1.multiply(BigDecimal.valueOf(expectedRate1Months))
                .add(rate2.multiply(BigDecimal.valueOf(expectedRate2Months)))
                .multiply(payer.totalArea ?: BigDecimal.ONE)
            // Service:
            val rent = ServiceEntity.rent1Service()
            val rentIds = ServiceDaoTest.insertService(ctx, db, rent)
            // Payer service:
            val payerRentId = PayerDaoTest.insertPayerService(
                db, payerId, rentIds.serviceId, fromServiceDate
            )

            // ACT
            // Payer service rates:
            insertRate(db, rent, payerRentId, rate1StartDate)
            insertRate(db, rent, payerRentId, rate2StartDate, rate2)

            // ASSERT
            rateDao.findSubtotalDebtsByPayerId(payerId).test {
                val subtotals = awaitItem()
                subtotals.forEach { it }
                assertThat(subtotals).hasSize(1)
                assertThat(subtotals[0].fullMonths).isEqualTo(
                    expectedRate1Months + expectedRate2Months
                )
                assertThat(subtotals[0].serviceDebt).isEquivalentAccordingToCompareTo(
                    expectedServiceDebt
                )
                cancel()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertHeatingRatesWithMeterAndFindSubtotalDebtsByPayerId_shouldReturn_correctServiceAndPayerServiceDebts_inFlow() =
        runTest {
            // ARRANGE
            // rates
            val rate1 = RateEntity.DEF_HEATING_RATE
            val rate2 = RateEntity.DEF_HEATING_RATE.add(BigDecimal.ONE)
            val meterPayerRate = RateEntity.DEF_HEATING_PAYER_RATE
            // service from date
            val fromServiceDate = fixCurrDateTime.minusMonths(6)
            // rate start dates
            val rate1StartDate = fixCurrDateTime.minusMonths(8)
            val rate2StartDate = fixCurrDateTime.minusMonths(2)
            // meter values dates
            val startMeterValueDate = fixCurrDateTime.minusMonths(10)
            val meterValue2Date = startMeterValueDate.plusMonths(1)
            val meterValue21Date = meterValue2Date.plusDays(15)
            val meterValue3Date = startMeterValueDate.plusMonths(2)
            val meterValue6Date = startMeterValueDate.plusMonths(6)
            val meterValue7Date = startMeterValueDate.plusMonths(7)
            val meterValue71Date = meterValue7Date.plusDays(23)
            val meterValue8Date = startMeterValueDate.plusMonths(8)
            val meterValue9Date = startMeterValueDate.plusMonths(9)
            // meter values
            val meterVal1 = MeterValueEntity.DEF_HEATING_VAL1
            val meterVal2 = MeterValueEntity.DEF_HEATING_VAL2
            val meterVal21 = MeterValueEntity.DEF_HEATING_VAL3.add(BigDecimal.ONE)
            val meterVal3 = meterVal21.add(meterVal21.subtract(meterVal1))
                .remainder(MeterEntity.DEF_HEATING_MAX_VAL)
            val meterVal6 = meterVal3.add(BigDecimal.ONE).remainder(MeterEntity.DEF_HEATING_MAX_VAL)
            val meterVal7 = meterVal6.add(BigDecimal.ONE).remainder(MeterEntity.DEF_HEATING_MAX_VAL)
            val meterVal71 =
                meterVal7.add(BigDecimal.ONE).remainder(MeterEntity.DEF_HEATING_MAX_VAL)
            val meterVal8 =
                meterVal71.add(BigDecimal.ONE).remainder(MeterEntity.DEF_HEATING_MAX_VAL)
            val meterVal9 = meterVal8.add(BigDecimal.ONE).remainder(MeterEntity.DEF_HEATING_MAX_VAL)
            // Payers:
            val payer = PayerEntity.favoritePayer(ctx, livingSpace = null)
            val payerId = PayerDaoTest.insertPayer(db, payer)
            val meterPayer = PayerEntity.payerWithTwoPersons(ctx)
            val meterPayerId = PayerDaoTest.insertPayer(db, meterPayer)
            // Service:
            val heating = ServiceEntity.heating6Service()
            val heatingIds = ServiceDaoTest.insertService(ctx, db, heating)
            // Payer services:
            PayerDaoTest.insertPayerService(
                db, payerId, heatingIds.serviceId, fromServiceDate
            )
            val meterPayerServiceId = PayerDaoTest.insertPayerService(
                db, meterPayerId, heatingIds.serviceId, fromServiceDate, isMeterOwner = true
            )
            // Meters:
            val heatingMeter = MeterEntity.heatingMeter(
                ctx, meterPayerId, fixCurrDateTime.minusMonths(4), MeterEntity.DEF_HEATING_INIT_VAL
            )
            val meterIds = MeterDaoTest.insertMeter(ctx, db, heatingMeter)
            // Meter values:
            val meterValues = listOf(
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = startMeterValueDate,
                    meterValue = meterVal1
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue2Date, meterValue = meterVal2
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue21Date,
                    meterValue = meterVal21
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue3Date, meterValue = meterVal3
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue6Date, meterValue = meterVal6
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue7Date, meterValue = meterVal7
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue71Date,
                    meterValue = meterVal71
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue8Date, meterValue = meterVal8
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue9Date, meterValue = meterVal9
                )
            )
            MeterDaoTest.insertMeterValues(db, heatingMeter, meterValues = meterValues)

            // EXPECTED:
            val expectedRate1Months = ChronoUnit.MONTHS.between(fromServiceDate, rate2StartDate)
            val expectedRate2Months =
                ChronoUnit.MONTHS.between(rate2StartDate, currentDateTime) + 1 // O
            val expectedServiceDebt = rate1.multiply(BigDecimal.valueOf(expectedRate1Months))
                .add(rate2.multiply(BigDecimal.valueOf(expectedRate2Months)))
                .multiply(payer.livingSpace ?: BigDecimal.ONE)

            // ACT
            // Service rates:
            insertRate(db, heating, startDate = rate1StartDate)
            insertRate(db, heating, startDate = rate2StartDate, rateValue = rate2)
            // Payer Service rate:
            insertRate(
                db, heating, meterPayerServiceId,
                startDate = rate1StartDate, rateValue = meterPayerRate
            )
            // LOGGING
            logRate(heating, rate1StartDate, rate1)
            logRate(heating, rate2StartDate, rate2)
            logRate(heating, rate1StartDate, meterPayerRate)

            // 1. ASSERT findServiceDebtsByPayerId
            rateDao.findSubtotalDebtsByPayerId(payerId).test {
                val subtotals = awaitItem()
                subtotals.forEach { println(it) }
                assertThat(subtotals).hasSize(1)
                assertThat(subtotals[0].fullMonths).isEqualTo(
                    expectedRate1Months + expectedRate2Months
                )
                assertThat(subtotals[0].serviceDebt).isEquivalentAccordingToCompareTo(
                    expectedServiceDebt
                )
                //assertThat(subtotals[0].serviceDebt.compareTo(expectedServiceDebt) == 0).isTrue()
                cancel()
            }
            // LOGGING
            var prevValue: MeterValueEntity? = null
            meterValues.forEach {
                MeterDaoTest.logMeterValueDiff(it, prevValue, MeterEntity.DEF_HEATING_MAX_VAL)
                if (prevValue == null || prevValue != it) prevValue = it
            }
            // EXPECTED:
            val missedMonths =
                BigDecimal.valueOf(ChronoUnit.MONTHS.between(meterValue3Date, meterValue6Date))
            println("Missed months: %.2f".format(missedMonths))
            val diff63 = meterVal6.add(meterVal3).divide(BigDecimal("2"))
            val livingSpace: BigDecimal = meterPayer.livingSpace!!
            val serviceDebts = listOf(
                //livingSpace.multiply(meterVal1.multiply(meterPayerRate)),
                //livingSpace.multiply(meterVal2.multiply(meterPayerRate)),
                livingSpace.multiply(meterVal3.multiply(meterPayerRate)),
                missedMonths.subtract(BigDecimal.ONE)
                    .multiply(livingSpace.multiply(diff63.multiply(meterPayerRate))),
                livingSpace.multiply(meterVal7.multiply(meterPayerRate)),
                livingSpace.multiply(meterVal8.multiply(meterPayerRate)),
                livingSpace.multiply(meterVal9.multiply(meterPayerRate))
            )
            val expectedMeterServiceDebt = serviceDebts.sumOf { it }
            serviceDebts.forEach { println(it) }
            // 2. ASSERT findServiceDebtsByPayerId
            rateDao.findSubtotalDebtsByPayerId(meterPayerId).test(timeout = 5000.milliseconds) {
                val subtotals = awaitItem()
                subtotals.forEach { println(it) }
                assertThat(subtotals).hasSize(1)
                assertThat(subtotals[0].serviceDebt).isEquivalentAccordingToCompareTo(
                    expectedMeterServiceDebt.setScale(5, RoundingMode.DOWN)
                )
                cancel()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertGarbageRatesAndFindSubtotalDebtsByPayerId_shouldReturn_correctPayerServiceDebt_inFlow() =
        runTest {
            // ARRANGE
            val rate1 = RateEntity.DEF_GARBAGE_PAYER_RATE
            val rate2 = RateEntity.DEF_GARBAGE_PAYER_RATE.add(BigDecimal.ONE)
            val fromServiceDate = currentDateTime.minusMonths(6)
            val rate1StartDate = currentDateTime.minusMonths(8)
            val rate2StartDate = currentDateTime.minusMonths(2)

            val expectedRate1Months = ChronoUnit.MONTHS.between(fromServiceDate, rate2StartDate)
            val expectedRate2Months = ChronoUnit.MONTHS.between(rate2StartDate, currentDateTime)

            val payer = PayerEntity.payerWithTwoPersons(ctx)
            val payerId = PayerDaoTest.insertPayer(db, payer)

            val expectedServiceDebt = rate1.multiply(BigDecimal.valueOf(expectedRate1Months))
                .add(rate2.multiply(BigDecimal.valueOf(expectedRate2Months)))
                .multiply(BigDecimal.valueOf(payer.personsNum.toLong()))
            // Service:
            val garbage = ServiceEntity.garbage8Service()
            val garbageIds = ServiceDaoTest.insertService(ctx, db, garbage)
            // Payer service:
            val payerGarbageId = PayerDaoTest.insertPayerService(
                db, payerId, garbageIds.serviceId, fromServiceDate
            )

            // ACT
            // Payer service rates:
            insertRate(db, garbage, payerGarbageId, rate1StartDate, isPerPerson = true)
            insertRate(db, garbage, payerGarbageId, rate2StartDate, rate2, isPerPerson = true)

            // ASSERT
            rateDao.findSubtotalDebtsByPayerId(payerId).test {
                val subtotals = awaitItem()
                assertThat(subtotals).hasSize(1)
                assertThat(subtotals[0].fullMonths).isEqualTo(
                    expectedRate1Months + expectedRate2Months
                )
                assertThat(subtotals[0].serviceDebt).isEquivalentAccordingToCompareTo(
                    expectedServiceDebt
                )
                cancel()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertInternetRatesAndFindSubtotalDebtsByPayerId_shouldReturn_correctPayerServiceDebt_inFlow() =
        runTest {
            // ARRANGE
            val rate1 = RateEntity.DEF_INTERNET_PAYER_RATE
            val rate2 = RateEntity.DEF_INTERNET_PAYER_RATE.add(BigDecimal.ONE)
            val fromServiceDate = currentDateTime.minusMonths(6)
            val rate1StartDate = currentDateTime.minusMonths(8)
            val rate2StartDate = currentDateTime.minusMonths(2)

            val expectedRate1Months = ChronoUnit.MONTHS.between(fromServiceDate, rate2StartDate)
            val expectedRate2Months = ChronoUnit.MONTHS.between(rate2StartDate, currentDateTime)
            // Payer:
            val payer = PayerEntity.payerWithTwoPersons(ctx)
            val payerId = PayerDaoTest.insertPayer(db, payer)

            val expectedServiceDebt = rate1.multiply(BigDecimal.valueOf(expectedRate1Months))
                .add(rate2.multiply(BigDecimal.valueOf(expectedRate2Months)))
            // Service:
            val internet = ServiceEntity.internet12Service()
            val internetIds = ServiceDaoTest.insertService(ctx, db, internet)
            // Payer service:
            val payerInternetId = PayerDaoTest.insertPayerService(
                db, payerId, internetIds.serviceId, fromServiceDate
            )

            // ACT
            // Payer service rates:
            insertRate(db, internet, payerInternetId, rate1StartDate)
            insertRate(db, internet, payerInternetId, rate2StartDate, rate2)

            // ASSERT
            rateDao.findSubtotalDebtsByPayerId(payerId).test {
                val subtotals = awaitItem()
                assertThat(subtotals).hasSize(1)
                assertThat(subtotals[0].fullMonths).isEqualTo(
                    expectedRate1Months + expectedRate2Months
                )
                assertThat(subtotals[0].serviceDebt).isEquivalentAccordingToCompareTo(
                    expectedServiceDebt
                )
                cancel()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertElectricityMeterRatesAndFindSubtotalDebtsByPayerId_shouldReturn_correctServiceDebt_inFlow() =
        runTest {
            // ARRANGE
            // rates
            val rate11 = RateEntity.DEF_ELECTRO_RANGE1_RATE
            val rate12 = RateEntity.DEF_ELECTRO_RANGE2_RATE
            val rate13 = RateEntity.DEF_ELECTRO_RANGE3_RATE
            val rate21 = RateEntity.DEF_ELECTRO_RANGE1_RATE.add(BigDecimal.ONE)
            val rate22 = RateEntity.DEF_ELECTRO_RANGE2_RATE.add(BigDecimal.ONE)
            val rate23 = RateEntity.DEF_ELECTRO_RANGE3_RATE.add(BigDecimal.ONE)
            // rates start dates
            val rate1StartDate = fixCurrDateTime.minusMonths(12)
            val rate2StartDate = fixCurrDateTime.minusMonths(5)
            // meter values dates
            val startMeterValueDate = fixCurrDateTime.minusMonths(10)
            val meterValue2Date = startMeterValueDate.plusMonths(1)
            val meterValue21Date = meterValue2Date.plusDays(15)
            val meterValue3Date = startMeterValueDate.plusMonths(2)
            val meterValue6Date = startMeterValueDate.plusMonths(6)
            val meterValue7Date = startMeterValueDate.plusMonths(7)
            val meterValue71Date = meterValue7Date.plusDays(23)
            val meterValue8Date = startMeterValueDate.plusMonths(8)
            val meterValue9Date = startMeterValueDate.plusMonths(9)
            // meter values
            val meterVal1 = MeterValueEntity.DEF_ELECTRO_VAL1
            val meterVal2 = MeterValueEntity.DEF_ELECTRO_VAL2
            val meterVal21 = MeterValueEntity.DEF_ELECTRO_VAL2.add(
                RateEntity.DEF_ELECTRO_RANGE2.divide(BigDecimal("2"))
            ).remainder(MeterEntity.DEF_ELECTRO_MAX_VAL)
            val meterVal3 = MeterValueEntity.DEF_ELECTRO_VAL3
            val meterVal6 =
                MeterValueEntity.DEF_ELECTRO_VAL4.add(RateEntity.DEF_ELECTRO_RANGE3.add(BigDecimal.ONE))
                    .remainder(MeterEntity.DEF_ELECTRO_MAX_VAL)
            val meterVal7 = meterVal6.add(RateEntity.DEF_ELECTRO_RANGE3.divide(BigDecimal("2")))
                .remainder(MeterEntity.DEF_ELECTRO_MAX_VAL)
            val meterVal71 = meterVal7.add(RateEntity.DEF_ELECTRO_RANGE3.subtract(BigDecimal.ONE))
                .remainder(MeterEntity.DEF_ELECTRO_MAX_VAL)
            val meterVal8 = meterVal71.add(RateEntity.DEF_ELECTRO_RANGE3.add(BigDecimal.ONE))
                .remainder(MeterEntity.DEF_ELECTRO_MAX_VAL)
            val meterVal9 = meterVal8.add(RateEntity.DEF_ELECTRO_RANGE3.add(BigDecimal.ONE))
                .remainder(MeterEntity.DEF_ELECTRO_MAX_VAL)
            // Payer:
            val payer = PayerEntity.payerWithTwoPersons(ctx)
            val payerId = PayerDaoTest.insertPayer(db, payer)

            // Service:
            val electricity = ServiceEntity.electricity2Service()
            val electricityIds = ServiceDaoTest.insertService(ctx, db, electricity)
            // Payer service:
            PayerDaoTest.insertPayerService(
                db, payerId, electricityIds.serviceId, isMeterOwner = true
            )
            // Meters:
            val electricityMeter = MeterEntity.electricityMeter(ctx, payerId, null, null)
            val meterIds = MeterDaoTest.insertMeter(ctx, db, electricityMeter)
            // Meter values:
            val meterValues = listOf(
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = startMeterValueDate,
                    meterValue = meterVal1
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue2Date, meterValue = meterVal2
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue21Date,
                    meterValue = meterVal21
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue3Date, meterValue = meterVal3
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue6Date, meterValue = meterVal6
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue7Date, meterValue = meterVal7
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue71Date,
                    meterValue = meterVal71
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue8Date, meterValue = meterVal8
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue9Date, meterValue = meterVal9
                )
            )
            MeterDaoTest.insertMeterValues(db, electricityMeter, meterValues = meterValues)

            // LOGGING
            logRate(electricity, rate1StartDate, rate11, rate12, rate13)
            logRate(electricity, rate2StartDate, rate21, rate22, rate23)
            var prevValue: MeterValueEntity? = null
            meterValues.forEach {
                MeterDaoTest.logMeterValueDiff(it, prevValue, MeterEntity.DEF_ELECTRO_MAX_VAL)
                if (prevValue == null || prevValue != it) prevValue = it
            }
            // EXPECTED:
            val missedMonths =
                BigDecimal.valueOf(ChronoUnit.MONTHS.between(meterValue3Date, meterValue6Date))
            println("Missed months: %.2f".format(missedMonths))
            val diff21 = meterVal2.subtract(meterVal1)
            val diff32 = MeterEntity.DEF_ELECTRO_MAX_VAL.subtract(meterVal2).add(meterVal3)
            val diff63 = meterVal6.subtract(meterVal3).divide(missedMonths)
            val diff76 = meterVal7.subtract(meterVal6)
            val diff87 = meterVal8.subtract(meterVal7)
            val diff98 = meterVal9.subtract(meterVal8)
            val serviceDebts = listOf(
                diff21.multiply(rate11),
                RateEntity.DEF_ELECTRO_RANGE2.multiply(rate11)
                    .add(diff32.subtract(RateEntity.DEF_ELECTRO_RANGE2).multiply(rate12)),
                RateEntity.DEF_ELECTRO_RANGE2.multiply(rate11)
                    .add(diff63.subtract(RateEntity.DEF_ELECTRO_RANGE2).multiply(rate12)),
                missedMonths.subtract(BigDecimal.ONE).multiply(
                    RateEntity.DEF_ELECTRO_RANGE2.multiply(rate21)
                        .add(diff63.subtract(RateEntity.DEF_ELECTRO_RANGE2).multiply(rate22))
                ),
                RateEntity.DEF_ELECTRO_RANGE2.multiply(rate21)
                    .add(diff76.subtract(RateEntity.DEF_ELECTRO_RANGE2).multiply(rate22)),
                RateEntity.DEF_ELECTRO_RANGE2.multiply(rate21)
                    .add(
                        RateEntity.DEF_ELECTRO_RANGE3.subtract(RateEntity.DEF_ELECTRO_RANGE2)
                            .multiply(rate22)
                    )
                    .add(diff87.subtract(RateEntity.DEF_ELECTRO_RANGE3).multiply(rate23)),
                RateEntity.DEF_ELECTRO_RANGE2.multiply(rate21)
                    .add(
                        RateEntity.DEF_ELECTRO_RANGE3.subtract(RateEntity.DEF_ELECTRO_RANGE2)
                            .multiply(rate22)
                    )
                    .add(diff98.subtract(RateEntity.DEF_ELECTRO_RANGE3).multiply(rate23))
            )
            val expectedServiceDebt = serviceDebts.sumOf { it }
            serviceDebts.forEach { println(it) }

            // ACT
            // Service rates:
            insertRate(
                db, electricity, startDate = rate1StartDate,
                rateValue = rate11, rateValue2 = rate12, rateValue3 = rate13
            )
            insertRate(
                db, electricity, startDate = rate2StartDate,
                rateValue = rate21, rateValue2 = rate22, rateValue3 = rate23
            )

            // ASSERT
            rateDao.findSubtotalDebtsByPayerId(payerId).test(timeout = 5000.milliseconds) {
                val subtotals = awaitItem()
                subtotals.forEach { println(it) }
                assertThat(subtotals).hasSize(1)
                assertThat(subtotals[0].serviceDebt).isEquivalentAccordingToCompareTo(
                    expectedServiceDebt
                )
                cancel()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertColdWaterMeterRatesWithMeterInitValuePassportDateControlAndFindSubtotalDebtsByPayerId_shouldReturn_correctServiceDebts_inFlow() =
        runTest {
            // 1. ARRANGE
            // rates
            val rate1 = RateEntity.DEF_COLD_WATER_RATE
            val rate2 = RateEntity.DEF_COLD_WATER_RATE.add(BigDecimal.ONE)
            // rates start dates
            val rate1StartDate = fixCurrDateTime.minusMonths(15)
            val rate2StartDate = fixCurrDateTime.minusMonths(5)
            // meter values dates
            val startMeterValueDate = fixCurrDateTime.minusMonths(10)
            val meterValue2Date = startMeterValueDate.plusMonths(1)
            val meterValue21Date = meterValue2Date.plusDays(15)
            val meterValue3Date = startMeterValueDate.plusMonths(2)
            val meterValue6Date = startMeterValueDate.plusMonths(6)
            val meterValue7Date = startMeterValueDate.plusMonths(7)
            val meterValue71Date = meterValue7Date.plusDays(23)
            val meterValue8Date = startMeterValueDate.plusMonths(8)
            val meterValue9Date = startMeterValueDate.plusMonths(9)
            // meter values
            val meterVal1 = MeterValueEntity.DEF_COLD_WATER_VAL1
            val meterVal2 = MeterValueEntity.DEF_COLD_WATER_VAL2
            val meterVal21 = MeterValueEntity.DEF_COLD_WATER_VAL3.add(BigDecimal.TEN)
            val meterVal3 = meterVal21.add(meterVal21.subtract(meterVal1))
                .remainder(MeterEntity.DEF_WATER_MAX_VAL)
            val meterVal6 = meterVal3.add(BigDecimal.TEN).remainder(MeterEntity.DEF_WATER_MAX_VAL)
            val meterVal7 = meterVal6.add(BigDecimal.TEN).remainder(MeterEntity.DEF_WATER_MAX_VAL)
            val meterVal71 = meterVal7.add(BigDecimal.TEN).remainder(MeterEntity.DEF_WATER_MAX_VAL)
            val meterVal8 = meterVal71.add(BigDecimal.TEN).remainder(MeterEntity.DEF_WATER_MAX_VAL)
            val meterVal9 = meterVal8.add(BigDecimal.TEN).remainder(MeterEntity.DEF_WATER_MAX_VAL)
            // Payers:
            val payer = PayerEntity.payerWithTwoPersons(ctx)
            val payerId = PayerDaoTest.insertPayer(db, payer)

            // Service:
            val coldWater = ServiceEntity.coldWater4Service()
            val coldWaterIds = ServiceDaoTest.insertService(ctx, db, coldWater)
            // Payer service:
            PayerDaoTest.insertPayerService(
                db, payerId, coldWaterIds.serviceId, isMeterOwner = true
            )
            // Meters:
            var passportDate = fixCurrDateTime.minusMonths(11)
            val coldWaterMeter = MeterEntity.coldWaterMeter(
                ctx, payerId, passportDate, MeterEntity.DEF_COLD_WATER_INIT_VAL
            )
            val meterIds = MeterDaoTest.insertMeter(ctx, db, coldWaterMeter)
            // Meter values:
            val meterValues = listOf(
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = startMeterValueDate,
                    meterValue = meterVal1
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue2Date, meterValue = meterVal2
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue21Date,
                    meterValue = meterVal21
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue3Date, meterValue = meterVal3
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue6Date, meterValue = meterVal6
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue7Date, meterValue = meterVal7
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue71Date,
                    meterValue = meterVal71
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue8Date, meterValue = meterVal8
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue9Date, meterValue = meterVal9
                )
            )
            MeterDaoTest.insertMeterValues(db, coldWaterMeter, meterValues = meterValues)

            // LOGGING
            logRate(coldWater, rate1StartDate, rate1)
            logRate(coldWater, rate2StartDate, rate2)
            var prevValue: MeterValueEntity? = null
            meterValues.forEach {
                MeterDaoTest.logMeterValueDiff(it, prevValue, MeterEntity.DEF_WATER_MAX_VAL)
                if (prevValue == null || prevValue != it) prevValue = it
            }
            // EXPECTED:
            val missedMonths =
                BigDecimal.valueOf(ChronoUnit.MONTHS.between(meterValue3Date, meterValue6Date))
            println("Missed months: %.2f".format(missedMonths))
            val diff21 = meterVal2.subtract(meterVal1)
            val diff32 = MeterEntity.DEF_WATER_MAX_VAL.subtract(meterVal2).add(meterVal3)
            val diff63 = meterVal6.subtract(meterVal3).divide(missedMonths)
            val diff76 = meterVal7.subtract(meterVal6)
            val diff87 = meterVal8.subtract(meterVal7)
            val diff98 = meterVal9.subtract(meterVal8)
            val serviceDebts = listOf(
                diff21.multiply(rate1), diff32.multiply(rate1),
                diff63.multiply(rate1),
                missedMonths.subtract(BigDecimal.ONE).multiply(diff63.multiply(rate2)),
                diff76.multiply(rate2), diff87.multiply(rate2),
                diff98.multiply(rate2)
            )
            val expectedServiceDebt = serviceDebts.sumOf { it }
            serviceDebts.forEach { println(it) }

            // ACT
            // Service rates:
            insertRate(db, coldWater, startDate = rate1StartDate, rateValue = rate1)
            insertRate(db, coldWater, startDate = rate2StartDate, rateValue = rate2)

            // 1. ASSERT findServiceDebtsByPayerId
            rateDao.findSubtotalDebtsByPayerId(payerId).test(timeout = 5000.milliseconds) {
                val subtotals = awaitItem()
                subtotals.forEach { println(it) }
                assertThat(subtotals).hasSize(1)
                assertThat(subtotals[0].serviceDebt).isEquivalentAccordingToCompareTo(
                    expectedServiceDebt
                )
                cancel()
            }

            // 2. ARRANGE
            // EXPECTED:
            passportDate = fixCurrDateTime.minusMonths(4)
            val updatedColdWaterMeter = MeterEntity.coldWaterMeter(
                ctx, payerId, passportDate, MeterEntity.DEF_COLD_WATER_INIT_VAL,
                meterId = coldWaterMeter.meterId
            )
            meterDao().update(updatedColdWaterMeter)
            println(updatedColdWaterMeter)
            val diff1i = meterVal1.subtract(MeterEntity.DEF_COLD_WATER_INIT_VAL)
            println(diff1i)
            val expectedServiceDebtWithInitValue = expectedServiceDebt.add(diff1i.multiply(rate1))

            // 2. ASSERT findServiceDebtsByPayerId
            rateDao.findSubtotalDebtsByPayerId(payerId).test(timeout = 5000.milliseconds) {
                val subtotals = awaitItem()
                subtotals.forEach { println(it) }
                assertThat(subtotals).hasSize(1)
                assertThat(subtotals[0].serviceDebt).isEquivalentAccordingToCompareTo(
                    expectedServiceDebtWithInitValue
                )
                cancel()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertHotWaterMeterRatesAndFindSubtotalDebtsByPayerId_shouldReturn_correctServiceDebts_inFlow() =
        runTest {
            // ARRANGE
            // rates
            val hotWaterRate1 = RateEntity.DEF_HOT_WATER_RATE
            val hotWaterRate2 = RateEntity.DEF_HOT_WATER_RATE.add(BigDecimal.ONE)
            val wasteRate1 = RateEntity.DEF_WASTE_RATE
            val wasteRate2 = RateEntity.DEF_WASTE_RATE.add(BigDecimal.ONE)
            // rates start dates
            val rate1StartDate = fixCurrDateTime.minusMonths(15)
            val rate2StartDate = fixCurrDateTime.minusMonths(5)
            // meter values dates
            val startMeterValueDate = fixCurrDateTime.minusMonths(10)
            val meterValue2Date = startMeterValueDate.plusMonths(1)
            val meterValue21Date = meterValue2Date.plusDays(15)
            val meterValue3Date = startMeterValueDate.plusMonths(2)
            val meterValue6Date = startMeterValueDate.plusMonths(6)
            val meterValue7Date = startMeterValueDate.plusMonths(7)
            val meterValue71Date = meterValue7Date.plusDays(23)
            val meterValue8Date = startMeterValueDate.plusMonths(8)
            val meterValue9Date = startMeterValueDate.plusMonths(9)
            // meter values
            val meterVal1 = MeterValueEntity.DEF_HOT_WATER_VAL1
            val meterVal2 = MeterValueEntity.DEF_HOT_WATER_VAL2
            val meterVal21 = MeterValueEntity.DEF_HOT_WATER_VAL3.add(BigDecimal.TEN)
            val meterVal3 = meterVal21.add(meterVal21.subtract(meterVal1))
                .remainder(MeterEntity.DEF_WATER_MAX_VAL)
            val meterVal6 = meterVal3.add(BigDecimal.TEN).remainder(MeterEntity.DEF_WATER_MAX_VAL)
            val meterVal7 = meterVal6.add(BigDecimal.TEN).remainder(MeterEntity.DEF_WATER_MAX_VAL)
            val meterVal71 = meterVal7.add(BigDecimal.TEN).remainder(MeterEntity.DEF_WATER_MAX_VAL)
            val meterVal8 = meterVal71.add(BigDecimal.TEN).remainder(MeterEntity.DEF_WATER_MAX_VAL)
            val meterVal9 = meterVal8.add(BigDecimal.TEN).remainder(MeterEntity.DEF_WATER_MAX_VAL)
            // Payer:
            val payer = PayerEntity.payerWithTwoPersons(ctx)
            val payerId = PayerDaoTest.insertPayer(db, payer)
            // Services:
            val waste = ServiceEntity.waste5Service()
            val wasteIds = ServiceDaoTest.insertService(ctx, db, waste)
            val hotWater = ServiceEntity.hotWater7Service()
            val hotWaterIds = ServiceDaoTest.insertService(ctx, db, hotWater)
            // Payer services:
            PayerDaoTest.insertPayerService(db, payerId, wasteIds.serviceId)
            PayerDaoTest.insertPayerService(
                db, payerId, hotWaterIds.serviceId, isMeterOwner = true
            )
            // Meter:
            val hotWaterMeter = MeterEntity.hotWaterMeter(
                ctx, payerId, fixCurrDateTime.minusMonths(4), MeterEntity.DEF_HOT_WATER_INIT_VAL
            )
            val meterIds = MeterDaoTest.insertMeter(ctx, db, hotWaterMeter)
            // Meter values:
            val meterValues = listOf(
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = startMeterValueDate,
                    meterValue = meterVal1
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue2Date, meterValue = meterVal2
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue21Date,
                    meterValue = meterVal21
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue3Date, meterValue = meterVal3
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue6Date, meterValue = meterVal6
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue7Date, meterValue = meterVal7
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue71Date,
                    meterValue = meterVal71
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue8Date, meterValue = meterVal8
                ),
                MeterValueEntity.defaultMeterValue(
                    meterId = meterIds.meterId, valueDate = meterValue9Date, meterValue = meterVal9
                )
            )
            MeterDaoTest.insertMeterValues(db, hotWaterMeter, meterValues = meterValues)

            // LOGGING
            logRate(hotWater, rate1StartDate, hotWaterRate1)
            logRate(waste, rate1StartDate, wasteRate1)
            logRate(hotWater, rate2StartDate, hotWaterRate2)
            logRate(waste, rate2StartDate, wasteRate2)
            var prevValue = MeterValueEntity.defaultMeterValue(
                meterId = meterIds.meterId,
                valueDate = hotWaterMeter.passportDate!!, meterValue = hotWaterMeter.initValue
            )
            meterValues.forEach {
                MeterDaoTest.logMeterValueDiff(it, prevValue, MeterEntity.DEF_WATER_MAX_VAL)
                if (prevValue != it) prevValue = it
            }
            // EXPECTED:
            val missedMonths =
                BigDecimal.valueOf(ChronoUnit.MONTHS.between(meterValue3Date, meterValue6Date))
            println("Missed months: %.2f".format(missedMonths))
            val diff1i = meterVal1.subtract(MeterEntity.DEF_HOT_WATER_INIT_VAL)
            val diff21 = meterVal2.subtract(meterVal1)
            val diff32 = MeterEntity.DEF_WATER_MAX_VAL.subtract(meterVal2).add(meterVal3)
            val diff63 = meterVal6.subtract(meterVal3).divide(missedMonths)
            val diff76 = meterVal7.subtract(meterVal6)
            val diff87 = meterVal8.subtract(meterVal7)
            val diff98 = meterVal9.subtract(meterVal8)
            val hotWaterDebts = listOf(
                diff1i.multiply(hotWaterRate1),
                diff21.multiply(hotWaterRate1), diff32.multiply(hotWaterRate1),
                diff63.multiply(hotWaterRate1),
                missedMonths.subtract(BigDecimal.ONE).multiply(diff63.multiply(hotWaterRate2)),
                diff76.multiply(hotWaterRate2), diff87.multiply(hotWaterRate2),
                diff98.multiply(hotWaterRate2)
            )
            val expectedHotWaterDebt = hotWaterDebts.sumOf { it }
            hotWaterDebts.forEach { println(it) }
            val wasteDebts = listOf(
                diff1i.multiply(wasteRate1),
                diff21.multiply(wasteRate1), diff32.multiply(wasteRate1),
                diff63.multiply(wasteRate1),
                missedMonths.subtract(BigDecimal.ONE).multiply(diff63.multiply(wasteRate2)),
                diff76.multiply(wasteRate2), diff87.multiply(wasteRate2),
                diff98.multiply(wasteRate2)
            )
            val expectedWasteDebt = wasteDebts.sumOf { it }
            wasteDebts.forEach { println(it) }

            // ACT
            // Service rates:
            insertRate(db, hotWater, startDate = rate1StartDate, rateValue = hotWaterRate1)
            insertRate(db, waste, startDate = rate1StartDate, rateValue = wasteRate1)
            insertRate(db, hotWater, startDate = rate2StartDate, rateValue = hotWaterRate2)
            insertRate(db, waste, startDate = rate2StartDate, rateValue = wasteRate2)

            // ASSERT
            rateDao.findSubtotalDebtsByPayerId(payerId).test(timeout = 5000.milliseconds) {
                val subtotals = awaitItem()
                subtotals.forEach { println(it) }
                assertThat(subtotals).hasSize(2)
                assertThat(subtotals[0].serviceDebt).isEquivalentAccordingToCompareTo(
                    expectedWasteDebt
                )
                assertThat(subtotals[1].serviceDebt).isEquivalentAccordingToCompareTo(
                    expectedHotWaterDebt
                )
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
            db: HomeDatabase, service: ServiceEntity, payerServiceId: UUID? = null,
            startDate: OffsetDateTime = OffsetDateTime.now(),
            rateValue: BigDecimal = BigDecimal.ZERO,
            isPerPerson: Boolean = false, isPrivileges: Boolean = false,
            rateValue2: BigDecimal = BigDecimal.ZERO,
            rateValue3: BigDecimal = BigDecimal.ZERO,
            privRateValue: BigDecimal = BigDecimal.ZERO
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
                            service.serviceId, payerServiceId, startDate, rateValue2
                        )
                    )
                    rates.add(
                        RateEntity.electricityRateFrom800(
                            service.serviceId, payerServiceId, startDate, rateValue3
                        )
                    )
                    rate = RateEntity.electricityPrivilegesRate(
                        service.serviceId, payerServiceId, startDate, privRateValue
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
                        service.serviceId,
                        payerServiceId,
                        startDate,
                        isPerPerson,
                        isPrivileges,
                        rateValue
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
            if (rates.isNotEmpty()) {
                db.rateDao().insert(rates)
                rates.forEach { println(it) }
                //return rates[0].rateId
            }
            db.rateDao().insert(rate)
            println(rate)
            return rate.rateId
        }

        fun logRate(
            service: ServiceEntity, rateStartDate: OffsetDateTime, rateValue: BigDecimal,
            rateValue2: BigDecimal = BigDecimal.ZERO, rateValue3: BigDecimal = BigDecimal.ZERO
        ) {
            val str = StringBuffer()
            str.append("Rate from ")
                .append(rateStartDate.format(DateTimeFormatter.ofPattern(Constants.APP_DAY_DATE_TIME)))
                .append(": ")
                .append(rateValue)
            when (service.serviceType) {
                ServiceType.ELECTRICITY -> {
                    str.append(RateEntity.DEF_ELECTRO_RANGE1).append(" - ")
                        .append(RateEntity.DEF_ELECTRO_RANGE2).append(" = ").append(rateValue)
                        .append("; ").append(RateEntity.DEF_ELECTRO_RANGE2)
                        .append(" - ").append(RateEntity.DEF_ELECTRO_RANGE3).append(" = ")
                        .append(rateValue2).append("; ")
                        .append(RateEntity.DEF_ELECTRO_RANGE3).append(" - ... = ")
                        .append(rateValue3)
                }
                else -> {}
            }
            println(str.toString())
        }
    }
}