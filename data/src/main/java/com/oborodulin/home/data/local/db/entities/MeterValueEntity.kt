package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.oborodulin.home.common.util.Utils
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@Entity(
    tableName = MeterValueEntity.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = MeterEntity::class,
        parentColumns = arrayOf("meterId"),
        childColumns = arrayOf("metersId"),
        onDelete = ForeignKey.CASCADE
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

        fun populateElectricityMeterValue1(meterId: UUID) = MeterValueEntity(
            metersId = meterId,
            valueDate = Utils.toOffsetDateTime("2022-06-19T14:29:10.212"),
            meterValue = BigDecimal.valueOf(9532)
        )

        fun populateElectricityMeterValue2(meterId: UUID) = MeterValueEntity(
            metersId = meterId,
            valueDate = Utils.toOffsetDateTime("2022-07-01T14:29:10.212"),
            meterValue = BigDecimal.valueOf(9558)
        )

        fun populateElectricityMeterValue3(meterId: UUID) = MeterValueEntity(
            metersId = meterId,
            valueDate = Utils.toOffsetDateTime("2022-08-01T14:29:10.212"),
            meterValue = BigDecimal.valueOf(9628)
        )

        fun populateColdWaterMeterValue1(meterId: UUID) = MeterValueEntity(
            metersId = meterId,
            valueDate = Utils.toOffsetDateTime("2022-06-19T14:29:10.212"),
            meterValue = BigDecimal.valueOf(1538)
        )

        fun populateColdWaterMeterValue2(meterId: UUID) = MeterValueEntity(
            metersId = meterId,
            valueDate = Utils.toOffsetDateTime("2022-07-01T14:29:10.212"),
            meterValue = BigDecimal.valueOf(1542)
        )

        fun populateColdWaterMeterValue3(meterId: UUID) = MeterValueEntity(
            metersId = meterId,
            valueDate = Utils.toOffsetDateTime("2022-08-01T14:29:10.212"),
            meterValue = BigDecimal.valueOf(1553)
        )
    }
}