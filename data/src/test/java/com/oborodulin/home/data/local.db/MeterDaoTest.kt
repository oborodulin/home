package com.oborodulin.home.data.local.db

import android.content.Context
import android.os.Build
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
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
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
private const val TAG = "Testing.db.MeterDaoTest"

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
            maxValue = BigDecimal("9999.999")
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
    fun insertMetersAndFindByPayerId_shouldReturn_theMeters_inFlow() = runTest {
        // ARRANGE
        val actualPayerId = PayerDaoTest.insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
        // ACT
        insertMeter(ctx, db, MeterEntity.electricityMeter(ctx, actualPayerId))
        insertMeter(ctx, db, MeterEntity.hotWaterMeter(ctx, actualPayerId))
        // ASSERT
        meterDao.findDistinctByPayerId(actualPayerId).test {
            assertThat(awaitItem()).hasSize(2)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertMeterInitValueWithMeterValuesAndFindMeterValuePaymentByPayerId_return_correctMeterValuePayments_inFlow() =
        runTest {
            // ARRANGE
            // Payer:
            val actualPayerId = PayerDaoTest.insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
            // Services:
            val electricityIds =
                ServiceDaoTest.insertService(ctx, db, ServiceEntity.electricity2Service())
            val coldWaterIds =
                ServiceDaoTest.insertService(ctx, db, ServiceEntity.coldWater4Service())
            // Payer services:
            PayerDaoTest.insertPayerService(
                db, actualPayerId, electricityIds.serviceId, isMeterOwner = true
            )
            PayerDaoTest.insertPayerService(
                db, actualPayerId, coldWaterIds.serviceId, isMeterOwner = true
            )
            // Meters:
            val electricityMeter = MeterEntity.electricityMeter(ctx, actualPayerId)
            insertMeter(ctx, db, electricityMeter)
            val coldWaterMeter = MeterEntity.coldWaterMeter(
                ctx, actualPayerId, currentDateTime, MeterEntity.DEF_COLD_WATER_INIT_VAL
            )
            insertMeter(ctx, db, coldWaterMeter)

            // ACT
            insertMeterValues(db, electricityMeter, currentDateTime)
            val coldWaterMeterValue = insertMeterValues(db, coldWaterMeter, currentDateTime)
            // Expected:
            // https://stackoverflow.com/questions/50890562/java-8-chronounit-months-betweenfromdate-todate-not-working-as-expected
            val coldWaterDiffMonths = ChronoUnit.MONTHS.between(
                coldWaterMeter.passportDate,
                coldWaterMeterValue[0].valueDate
            ) + 1 // Half-Open

            // ASSERT
            meterDao.findDistinctMeterValuePaymentByPayerId(actualPayerId).test {
                val meterValuePayments = awaitItem()
                meterValuePayments.forEach {
                    println("meterValuePayments: %s".format(it))
                }
                assertThat(meterValuePayments).hasSize(8)
                // Electricity Meter:
                assertThat(meterValuePayments[0].meterId).isEqualTo(electricityMeter.meterId)
                assertThat(meterValuePayments[0].meterValueId).isNotNull()
                assertThat(meterValuePayments[0].startMeterValue).isEqualTo(MeterValueEntity.DEF_ELECTRO_VAL1)
                assertThat(meterValuePayments[0].endMeterValue).isEqualTo(MeterValueEntity.DEF_ELECTRO_VAL2)
                assertThat(meterValuePayments[0].diffMeterValue).isEqualTo(
                    MeterValueEntity.DEF_ELECTRO_VAL2.subtract(MeterValueEntity.DEF_ELECTRO_VAL1)
                )
                assertThat(meterValuePayments[0].diffMonths).isEqualTo(1)
                assertThat(meterValuePayments[1].startMeterValue).isEqualTo(MeterValueEntity.DEF_ELECTRO_VAL2)
                assertThat(meterValuePayments[1].endMeterValue).isEqualTo(MeterValueEntity.DEF_ELECTRO_VAL3)
                assertThat(meterValuePayments[1].diffMonths).isEqualTo(1)
                assertThat(meterValuePayments[2].startMeterValue).isEqualTo(MeterValueEntity.DEF_ELECTRO_VAL3)
                assertThat(meterValuePayments[2].endMeterValue).isEqualTo(MeterValueEntity.DEF_ELECTRO_VAL4)
                assertThat(meterValuePayments[2].diffMonths).isEqualTo(1)
                assertThat(meterValuePayments[3].startMeterValue).isEqualTo(MeterValueEntity.DEF_ELECTRO_VAL4)
                assertThat(meterValuePayments[3].endMeterValue).isNull()
                assertThat(meterValuePayments[3].diffMonths).isEqualTo(0)
                assertThat(meterValuePayments[3].meterValueId).isNull()
                // Cold Water Meter:
                assertThat(meterValuePayments[4].meterId).isEqualTo(coldWaterMeter.meterId)
                assertThat(meterValuePayments[4].meterValueId).isNotNull()
                assertThat(meterValuePayments[4].startMeterValue).isEqualTo(MeterEntity.DEF_COLD_WATER_INIT_VAL)
                assertThat(meterValuePayments[4].endMeterValue).isEqualTo(MeterValueEntity.DEF_COLD_WATER_VAL1)
                assertThat(meterValuePayments[4].diffMeterValue).isEqualTo(
                    MeterValueEntity.DEF_COLD_WATER_VAL1.subtract(MeterEntity.DEF_COLD_WATER_INIT_VAL)
                        .divide(BigDecimal.valueOf(coldWaterDiffMonths))
                )
                assertThat(meterValuePayments[4].diffMonths).isEqualTo(coldWaterDiffMonths)
                assertThat(meterValuePayments[5].startMeterValue).isEqualTo(MeterValueEntity.DEF_COLD_WATER_VAL1)
                assertThat(meterValuePayments[5].endMeterValue).isEqualTo(MeterValueEntity.DEF_COLD_WATER_VAL2)
                assertThat(meterValuePayments[5].diffMonths).isEqualTo(1)
                assertThat(meterValuePayments[6].startMeterValue).isEqualTo(MeterValueEntity.DEF_COLD_WATER_VAL2)
                assertThat(meterValuePayments[6].endMeterValue).isEqualTo(MeterValueEntity.DEF_COLD_WATER_VAL3)
                assertThat(meterValuePayments[6].diffMonths).isEqualTo(1)
                assertThat(meterValuePayments[7].startMeterValue).isEqualTo(MeterValueEntity.DEF_COLD_WATER_VAL3)
                assertThat(meterValuePayments[7].endMeterValue).isNull()
                assertThat(meterValuePayments[7].diffMonths).isEqualTo(-1)
                assertThat(meterValuePayments[7].meterValueId).isNull()

                cancel()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertMeterValuesAndFindPrevMetersValuesByPayerId_return_correctPrevMetersValues_inFlow() =
        runTest {
            // ARRANGE
            val expectedPrevMonth =
                currentDateTime.minusMonths(1).withDayOfMonth(1).truncatedTo(ChronoUnit.SECONDS)
            val expectedPassportDate =
                currentDateTime.minusMonths(7).truncatedTo(ChronoUnit.SECONDS)
            // Payer:
            val actualPayerId = PayerDaoTest.insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
            // Services:
            val electricityIds =
                ServiceDaoTest.insertService(ctx, db, ServiceEntity.electricity2Service())
            val gasIds = ServiceDaoTest.insertService(ctx, db, ServiceEntity.gas3Service())
            val coldWaterIds =
                ServiceDaoTest.insertService(ctx, db, ServiceEntity.coldWater4Service())
            val wasteIds = ServiceDaoTest.insertService(ctx, db, ServiceEntity.waste5Service())
            val heatingIds =
                ServiceDaoTest.insertService(ctx, db, ServiceEntity.heating6Service())
            val hotWaterIds =
                ServiceDaoTest.insertService(ctx, db, ServiceEntity.hotWater7Service())
            // Payer services:
            PayerDaoTest.insertPayerService(
                db, actualPayerId, electricityIds.serviceId, isMeterOwner = true
            )
            PayerDaoTest.insertPayerService(
                db, actualPayerId, gasIds.serviceId, isMeterOwner = true
            )
            PayerDaoTest.insertPayerService(
                db, actualPayerId, coldWaterIds.serviceId, isMeterOwner = true
            )
            PayerDaoTest.insertPayerService(db, actualPayerId, wasteIds.serviceId)
            PayerDaoTest.insertPayerService(
                db, actualPayerId, heatingIds.serviceId, isMeterOwner = true
            )
            PayerDaoTest.insertPayerService(
                db, actualPayerId, hotWaterIds.serviceId, isMeterOwner = true
            )
            // Meters:
            val electricityMeter = MeterEntity.electricityMeter(ctx, actualPayerId)
            insertMeter(ctx, db, electricityMeter)
            val gasMeter = MeterEntity.gasMeter(ctx, actualPayerId, currentDateTime)
            insertMeter(ctx, db, gasMeter)
            val coldWaterMeter = MeterEntity.coldWaterMeter(ctx, actualPayerId, currentDateTime)
            insertMeter(ctx, db, coldWaterMeter)
            val heatingMeter = MeterEntity.heatingMeter(ctx, actualPayerId, currentDateTime)
            insertMeter(ctx, db, heatingMeter)
            val hotWaterMeter = MeterEntity.hotWaterMeter(ctx, actualPayerId, currentDateTime)
            insertMeter(ctx, db, hotWaterMeter)

            // ACT
            insertMeterValues(db, electricityMeter, currentDateTime)
            insertMeterValues(db, gasMeter, currentDateTime)
            insertMeterValues(db, coldWaterMeter, currentDateTime)
            insertMeterValues(db, heatingMeter, currentDateTime)
            // ASSERT
            meterDao.findDistinctPrevMetersValuesByPayerId(actualPayerId).test {
                val prevMeterValues = awaitItem()
                //prevMeterValues.forEach { println(it) }
                assertThat(prevMeterValues).hasSize(5)

                assertThat(prevMeterValues[0].serviceId).isEqualTo(electricityIds.serviceId)
                assertThat(prevMeterValues[0].prevValue).isEqualTo(MeterValueEntity.DEF_ELECTRO_VAL4)
                assertThat(prevMeterValues[0].prevLastDate).isEqualTo(expectedPrevMonth)
                assertThat(prevMeterValues[0].currentValue).isNull()

                assertThat(prevMeterValues[1].serviceId).isEqualTo(gasIds.serviceId)
                assertThat(prevMeterValues[1].prevValue).isEqualTo(MeterValueEntity.DEF_GAS_VAL3)
                assertThat(prevMeterValues[1].prevLastDate).isEqualTo(expectedPrevMonth)
                assertThat(prevMeterValues[1].currentValue).isNull()

                assertThat(prevMeterValues[2].serviceId).isEqualTo(coldWaterIds.serviceId)
                assertThat(prevMeterValues[2].prevValue).isEqualTo(MeterValueEntity.DEF_COLD_WATER_VAL2)
                assertThat(prevMeterValues[2].prevLastDate).isEqualTo(expectedPrevMonth)
                assertThat(prevMeterValues[2].currentValue).isEqualTo(MeterValueEntity.DEF_COLD_WATER_VAL3)

                assertThat(prevMeterValues[3].serviceId).isEqualTo(heatingIds.serviceId)
                assertThat(prevMeterValues[3].prevValue).isEqualTo(MeterValueEntity.DEF_HEATING_VAL2)
                assertThat(prevMeterValues[3].prevLastDate).isEqualTo(expectedPrevMonth)
                assertThat(prevMeterValues[3].currentValue).isEqualTo(MeterValueEntity.DEF_HEATING_VAL3)

                assertThat(prevMeterValues[4].serviceId).isEqualTo(hotWaterIds.serviceId)
                assertThat(prevMeterValues[4].prevValue).isEqualTo(MeterEntity.DEF_HOT_WATER_INIT_VAL)
                assertThat(prevMeterValues[4].prevLastDate).isEqualTo(expectedPassportDate)
                assertThat(prevMeterValues[4].currentValue).isNull()

                cancel()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteCurrentValueAndFindPrevMetersValuesByPayerId_return_correctPrevMetersValues_inFlow() =
        runTest {
            // ARRANGE
            val actualPayerId = PayerDaoTest.insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
            val coldWaterIds =
                ServiceDaoTest.insertService(ctx, db, ServiceEntity.coldWater4Service())
            PayerDaoTest.insertPayerService(
                db, actualPayerId, coldWaterIds.serviceId, isMeterOwner = true
            )
            val coldWaterMeter = MeterEntity.coldWaterMeter(ctx, actualPayerId)
            val coldWaterMeterIds = insertMeter(ctx, db, coldWaterMeter)
            insertMeterValues(db, coldWaterMeter, currentDateTime)
            // 1. ASSERT
            meterDao.findDistinctPrevMetersValuesByPayerId(actualPayerId).test {
                val prevMeterValues = awaitItem()
                assertThat(prevMeterValues[0].serviceId).isEqualTo(coldWaterIds.serviceId)
                assertThat(prevMeterValues[0].prevValue).isEqualTo(MeterValueEntity.DEF_COLD_WATER_VAL2)
                assertThat(prevMeterValues[0].currentValue).isEqualTo(MeterValueEntity.DEF_COLD_WATER_VAL3)
                cancel()
            }
            // ACT
            meterDao.deleteCurrentValue(coldWaterMeterIds.meterId)
            // 2. ASSERT
            meterDao.findDistinctPrevMetersValuesByPayerId(actualPayerId).test {
                val prevMeterValues = awaitItem()
                assertThat(prevMeterValues).hasSize(1)
                assertThat(prevMeterValues[0].serviceId).isEqualTo(coldWaterIds.serviceId)
                assertThat(prevMeterValues[0].prevValue).isEqualTo(MeterValueEntity.DEF_COLD_WATER_VAL2)
                assertThat(prevMeterValues[0].currentValue).isNull()
                cancel()
            }
        }

    companion object {
        suspend fun insertMeter(
            ctx: Context, db: HomeDatabase, meter: MeterEntity = MeterEntity.defaultMeter()
        ): MeterIds {
            val meterTl = MeterTlEntity.meterTl(ctx, meter.meterType, meter.meterId)
            db.meterDao().insert(meter, meterTl)
            println(meter)
            return MeterIds(meterId = meter.meterId, meterTlId = meterTl.meterTlId)
        }

        suspend fun insertMeterValues(
            db: HomeDatabase, meter: MeterEntity = MeterEntity.defaultMeter(),
            currDate: OffsetDateTime = OffsetDateTime.now(),
            meterValues: List<MeterValueEntity> = emptyList()
        ): List<MeterValueEntity> {
            if (meterValues.isNotEmpty()) {
                meterValues.forEach { db.meterDao().insert(it) }
                return meterValues
            }
            var values: List<MeterValueEntity> = emptyList()
            when (meter.meterType) {
                MeterType.ELECTRICITY -> {
                    values = listOf(
                        MeterValueEntity.electricityMeterValue1(
                            meterId = meter.meterId, currDate = currDate
                        ),
                        MeterValueEntity.electricityMeterValue2(
                            meterId = meter.meterId, currDate = currDate
                        ),
                        MeterValueEntity.electricityMeterValue3(
                            meterId = meter.meterId, currDate = currDate
                        ),
                        MeterValueEntity.electricityMeterValue4(
                            meterId = meter.meterId, currDate = currDate
                        ),
                    )
                    db.meterDao().insert(values[0], values[1], values[2], values[3])
                }
                MeterType.GAS -> {
                    values = listOf(
                        MeterValueEntity.gasMeterValue1(
                            meterId = meter.meterId, currDate = currDate
                        ), MeterValueEntity.gasMeterValue2(
                            meterId = meter.meterId, currDate = currDate
                        ), MeterValueEntity.gasMeterValue3(
                            meterId = meter.meterId, currDate = currDate
                        )
                    )
                    db.meterDao().insert(values[0], values[1], values[2])
                }
                MeterType.COLD_WATER -> {
                    values = listOf(
                        MeterValueEntity.coldWaterMeterValue1(
                            meterId = meter.meterId, currDate = currDate
                        ), MeterValueEntity.coldWaterMeterValue2(
                            meterId = meter.meterId, currDate = currDate
                        ), MeterValueEntity.coldWaterMeterValue3(
                            meterId = meter.meterId, currDate = currDate
                        )
                    )
                    db.meterDao().insert(values[0], values[1], values[2])
                }
                MeterType.HEATING -> {
                    values = listOf(
                        MeterValueEntity.heatingMeterValue1(
                            meterId = meter.meterId, currDate = currDate
                        ), MeterValueEntity.heatingMeterValue2(
                            meterId = meter.meterId, currDate = currDate
                        ),
                        MeterValueEntity.heatingMeterValue3(
                            meterId = meter.meterId, currDate = currDate
                        )
                    )
                    db.meterDao().insert(values[0], values[1], values[2])
                }
                MeterType.HOT_WATER -> {
                    values = listOf(
                        MeterValueEntity.hotWaterMeterValue1(
                            meterId = meter.meterId, currDate = currDate
                        ), MeterValueEntity.hotWaterMeterValue2(
                            meterId = meter.meterId, currDate = currDate
                        ),
                        MeterValueEntity.hotWaterMeterValue3(
                            meterId = meter.meterId, currDate = currDate
                        )
                    )
                    db.meterDao().insert(values[0], values[1], values[2])
                }
                MeterType.NONE -> {}
            }
            return values
        }

        fun logMeterValueDiff(
            value: MeterValueEntity, prevValue: MeterValueEntity? = null,
            meterMaxValue: BigDecimal? = null
        ) {
            val str = StringBuffer()
            str.append("Meter value at ").append(value.valueDate.year).append("-")
                .append(value.valueDate.monthValue).append("-").append(value.valueDate.dayOfMonth)
                .append(": ").append(value.meterValue)
            prevValue?.let {
                str.append(" [diff = ").append(
                    if (value.meterValue!! > it.meterValue) value.meterValue!!.subtract(it.meterValue)
                    else meterMaxValue?.subtract(it.meterValue)?.add(value.meterValue)
                ).append("]")
            }
            println(str.toString())
        }
    }
}