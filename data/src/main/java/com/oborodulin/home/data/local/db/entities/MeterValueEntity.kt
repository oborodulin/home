package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.oborodulin.home.common.data.entities.BaseEntity
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Entity(
    tableName = MeterValueEntity.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = MeterEntity::class,
        parentColumns = arrayOf("meterId"),
        childColumns = arrayOf("metersId"),
        onDelete = ForeignKey.CASCADE,
        deferred = true
    )]
)
data class MeterValueEntity(
    @PrimaryKey var meterValueId: UUID = UUID.randomUUID(),
    val valueDate: OffsetDateTime = OffsetDateTime.now(),
    val meterValue: BigDecimal? = null,
    @ColumnInfo(index = true) var metersId: UUID,
) : BaseEntity() {

    companion object {
        const val TABLE_NAME = "meter_values"

        // Electricity
        val DEF_ELECTRO_VAL1: BigDecimal = when (MeterEntity.DEF_ELECTRO_INIT_VAL) {
            null -> BigDecimal("9532")
            else -> MeterEntity.DEF_ELECTRO_INIT_VAL.add(
                RateEntity.DEF_ELECTRO_RANGE2.subtract(BigDecimal.ONE)
            ).remainder(MeterEntity.DEF_ELECTRO_MAX_VAL)
        }
        val DEF_ELECTRO_VAL2: BigDecimal =
            DEF_ELECTRO_VAL1.add(RateEntity.DEF_ELECTRO_RANGE2.subtract(BigDecimal.ONE))
                .remainder(MeterEntity.DEF_ELECTRO_MAX_VAL)
        val DEF_ELECTRO_VAL3: BigDecimal =
            DEF_ELECTRO_VAL2.add(RateEntity.DEF_ELECTRO_RANGE3.subtract(BigDecimal.ONE))
                .remainder(MeterEntity.DEF_ELECTRO_MAX_VAL)
        val DEF_ELECTRO_VAL4: BigDecimal =
            DEF_ELECTRO_VAL3.add(RateEntity.DEF_ELECTRO_RANGE3.add(BigDecimal.ONE))
                .remainder(MeterEntity.DEF_ELECTRO_MAX_VAL)

        // Gas
        val DEF_GAS_VAL1: BigDecimal = when (MeterEntity.DEF_GAS_INIT_VAL) {
            null -> BigDecimal("0.695")
            else -> MeterEntity.DEF_GAS_INIT_VAL.add(BigDecimal("0.1"))
        }
        val DEF_GAS_VAL2: BigDecimal = DEF_GAS_VAL1.add(BigDecimal("0.15"))
        val DEF_GAS_VAL3: BigDecimal = DEF_GAS_VAL2.add(BigDecimal("0.2"))

        // Cold water
        val DEF_COLD_WATER_VAL1: BigDecimal = when (MeterEntity.DEF_COLD_WATER_INIT_VAL) {
            null -> BigDecimal("1523.125")
            else -> MeterEntity.DEF_COLD_WATER_INIT_VAL.add(BigDecimal("5.132"))
                .remainder(MeterEntity.DEF_WATER_MAX_VAL)
        }
        val DEF_COLD_WATER_VAL2: BigDecimal =
            DEF_COLD_WATER_VAL1.add(BigDecimal("4.154")).remainder(MeterEntity.DEF_WATER_MAX_VAL)
        val DEF_COLD_WATER_VAL3: BigDecimal =
            DEF_COLD_WATER_VAL2.add(BigDecimal("3.912")).remainder(MeterEntity.DEF_WATER_MAX_VAL)

        // Hot water
        val DEF_HOT_WATER_VAL1: BigDecimal = when (MeterEntity.DEF_HOT_WATER_INIT_VAL) {
            null -> BigDecimal("2145.755")
            else -> MeterEntity.DEF_HOT_WATER_INIT_VAL.add(BigDecimal("3.132"))
                .remainder(MeterEntity.DEF_WATER_MAX_VAL)
        }
        val DEF_HOT_WATER_VAL2: BigDecimal =
            DEF_HOT_WATER_VAL1.add(BigDecimal("2.154")).remainder(MeterEntity.DEF_WATER_MAX_VAL)
        val DEF_HOT_WATER_VAL3: BigDecimal =
            DEF_HOT_WATER_VAL2.add(BigDecimal("1.912")).remainder(MeterEntity.DEF_WATER_MAX_VAL)

        // Heating
        val DEF_HEATING_VAL1: BigDecimal = when (MeterEntity.DEF_HEATING_INIT_VAL) {
            null -> BigDecimal("0.02113")
            else -> MeterEntity.DEF_HEATING_INIT_VAL.add(BigDecimal("0.01325"))
                .remainder(MeterEntity.DEF_HEATING_MAX_VAL)
        }
        val DEF_HEATING_VAL2: BigDecimal =
            DEF_HEATING_VAL1.add(BigDecimal("0.00957")).remainder(MeterEntity.DEF_HEATING_MAX_VAL)
        val DEF_HEATING_VAL3: BigDecimal =
            DEF_HEATING_VAL2.add(BigDecimal("0.02004")).remainder(MeterEntity.DEF_HEATING_MAX_VAL)

        fun defaultMeterValue(
            meterId: UUID, meterValueId: UUID = UUID.randomUUID(),
            valueDate: OffsetDateTime = OffsetDateTime.now(), meterValue: BigDecimal? = null
        ) = MeterValueEntity(
            metersId = meterId, meterValueId = meterValueId,
            valueDate = valueDate, meterValue = meterValue
        )

        fun electricityMeterValue1(
            meterId: UUID, currDate: OffsetDateTime, meterValue: BigDecimal? = DEF_ELECTRO_VAL1
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = currDate.minusMonths(4).withDayOfMonth(1),
            meterValue = meterValue
        )

        fun electricityMeterValue2(
            meterId: UUID, currDate: OffsetDateTime, meterValue: BigDecimal? = DEF_ELECTRO_VAL2
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = currDate.minusMonths(3).withDayOfMonth(1),
            meterValue = meterValue
        )

        fun electricityMeterValue3(
            meterId: UUID, currDate: OffsetDateTime, meterValue: BigDecimal? = DEF_ELECTRO_VAL3
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = currDate.minusMonths(2).withDayOfMonth(1),
            meterValue = meterValue
        )

        fun electricityMeterValue4(
            meterId: UUID, currDate: OffsetDateTime, meterValue: BigDecimal? = DEF_ELECTRO_VAL4
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = currDate.minusMonths(1).withDayOfMonth(1),
            meterValue = meterValue
        )

        fun gasMeterValue1(
            meterId: UUID, currDate: OffsetDateTime, meterValue: BigDecimal? = DEF_GAS_VAL1
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = currDate.minusMonths(3).withDayOfMonth(1),
            meterValue = meterValue
        )

        fun gasMeterValue2(
            meterId: UUID, currDate: OffsetDateTime, meterValue: BigDecimal? = DEF_GAS_VAL2
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = currDate.minusMonths(2).withDayOfMonth(1),
            meterValue = meterValue
        )

        fun gasMeterValue3(
            meterId: UUID, currDate: OffsetDateTime, meterValue: BigDecimal? = DEF_GAS_VAL3
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = currDate.minusMonths(1).withDayOfMonth(1),
            meterValue = meterValue
        )

        fun coldWaterMeterValue1(
            meterId: UUID, currDate: OffsetDateTime, meterValue: BigDecimal? = DEF_COLD_WATER_VAL1
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = currDate.minusMonths(2).withDayOfMonth(1),
            meterValue = meterValue
        )

        fun coldWaterMeterValue2(
            meterId: UUID, currDate: OffsetDateTime, meterValue: BigDecimal? = DEF_COLD_WATER_VAL2
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = currDate.minusMonths(1).withDayOfMonth(1),
            meterValue = meterValue
        )

        fun coldWaterMeterValue3(
            meterId: UUID, currDate: OffsetDateTime, meterValue: BigDecimal? = DEF_COLD_WATER_VAL3
        ) = defaultMeterValue(
            meterId = meterId, valueDate = currDate.withDayOfMonth(1), meterValue = meterValue
        )

        fun hotWaterMeterValue1(
            meterId: UUID, currDate: OffsetDateTime, meterValue: BigDecimal? = DEF_HOT_WATER_VAL1
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = currDate.minusMonths(2).withDayOfMonth(1), meterValue = meterValue
        )

        fun hotWaterMeterValue2(
            meterId: UUID, currDate: OffsetDateTime, meterValue: BigDecimal? = DEF_HOT_WATER_VAL2
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = currDate.minusMonths(1).withDayOfMonth(1), meterValue = meterValue
        )

        fun hotWaterMeterValue3(
            meterId: UUID, currDate: OffsetDateTime, meterValue: BigDecimal? = DEF_HOT_WATER_VAL3
        ) = defaultMeterValue(
            meterId = meterId, valueDate = currDate.withDayOfMonth(1), meterValue = meterValue
        )

        fun heatingMeterValue1(
            meterId: UUID, currDate: OffsetDateTime, meterValue: BigDecimal? = DEF_HEATING_VAL1
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = currDate.minusMonths(2).withDayOfMonth(1), meterValue = meterValue
        )

        fun heatingMeterValue2(
            meterId: UUID, currDate: OffsetDateTime, meterValue: BigDecimal? = DEF_HEATING_VAL2
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = currDate.minusMonths(1).withDayOfMonth(1), meterValue = meterValue
        )

        fun heatingMeterValue3(
            meterId: UUID, currDate: OffsetDateTime, meterValue: BigDecimal? = DEF_HEATING_VAL3
        ) = defaultMeterValue(
            meterId = meterId, valueDate = currDate.withDayOfMonth(1), meterValue = meterValue
        )
    }

    override fun id() = this.meterValueId

    override fun toString(): String {
        val str = StringBuffer()
        str.append("Meter value Entity at ")
            .append(DateTimeFormatter.ISO_LOCAL_DATE.format(valueDate))
            .append(": ").append(meterValue).append(" for  [metersId = ").append(metersId)
            .append("] meterValueId = ").append(meterValueId)
        return str.toString()
    }
}