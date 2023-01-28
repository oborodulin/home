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

        fun populateElectricityMeterValue1(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = MeterValueEntity(
            metersId = meterId,
            valueDate = valueDate,//Utils.toOffsetDateTime("2022-06-19T14:29:10.212"),
            meterValue = meterValue ?: BigDecimal.valueOf(9532)
        )

        fun populateElectricityMeterValue2(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = MeterValueEntity(
            metersId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(9558)
        )

        fun populateElectricityMeterValue3(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = MeterValueEntity(
            metersId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(9628)
        )

        fun populateColdWaterMeterValue1(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = MeterValueEntity(
            metersId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(1538)
        )

        fun populateColdWaterMeterValue2(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = MeterValueEntity(
            metersId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(1542)
        )

        fun populateColdWaterMeterValue3(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = MeterValueEntity(
            metersId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(1553)
        )

        fun populateHotWaterMeterValue1(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = MeterValueEntity(
            metersId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(2156)
        )

        fun populateHotWaterMeterValue2(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = MeterValueEntity(
            metersId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(2160)
        )

        fun populateHotWaterMeterValue3(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = MeterValueEntity(
            metersId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(2166)
        )

        fun populateHeatingMeterValue1(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = MeterValueEntity(
            metersId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(0.02185)
        )

        fun populateHeatingMeterValue2(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = MeterValueEntity(
            metersId = meterId,
            valueDate = valueDate,
            meterValue = meterValue ?: BigDecimal.valueOf(0.02254)
        )

        fun populateHeatingMeterValue3(
            meterId: UUID,
            valueDate: OffsetDateTime,
            meterValue: BigDecimal? = null
        ) = MeterValueEntity(
            metersId = meterId,
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