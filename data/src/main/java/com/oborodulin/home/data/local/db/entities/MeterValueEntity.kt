package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.OffsetDateTime
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
) {
    companion object {
        const val TABLE_NAME = "meter_values"

        fun defaultMeterValue(
            meterId: UUID,
            meterValueId: UUID = UUID.randomUUID(),
            valueDate: OffsetDateTime = OffsetDateTime.now(),
            meterValue: BigDecimal? = null
        ) = MeterValueEntity(
            metersId = meterId,
            meterValueId = meterValueId,
            valueDate = valueDate,
            meterValue = meterValue
        )

        fun electricityMeterValue1(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(9532)
        )

        fun electricityMeterValue2(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(9558)
        )

        fun electricityMeterValue3(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(9628)
        )

        fun coldWaterMeterValue1(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(1538)
        )

        fun coldWaterMeterValue2(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(1542)
        )

        fun coldWaterMeterValue3(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(1553)
        )

        fun hotWaterMeterValue1(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(2156)
        )

        fun hotWaterMeterValue2(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(2160)
        )

        fun hotWaterMeterValue3(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(2166)
        )

        fun heatingMeterValue1(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(0.02185)
        )

        fun heatingMeterValue2(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(0.02254)
        )

        fun heatingMeterValue3(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = defaultMeterValue(
            meterId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(0.02394)
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MeterValueEntity
        if (meterValueId != other.meterValueId) return false

        return true
    }

    override fun hashCode(): Int {
        return meterValueId.hashCode()
    }
}