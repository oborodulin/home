package com.oborodulin.home.data.local.db.entities

import android.content.Context
import androidx.room.*
import java.util.*

@Entity(
    tableName = MeterTlEntity.TABLE_NAME,
    indices = [Index(value = ["meterLocCode", "metersId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = MeterEntity::class,
        parentColumns = arrayOf("meterId"),
        childColumns = arrayOf("metersId"),
        onDelete = ForeignKey.CASCADE,
        deferred = true
    )]
)
data class MeterTlEntity(
    @PrimaryKey val meterTlId: UUID = UUID.randomUUID(),
    val meterLocCode: String = Locale.getDefault().language,
    val meterMeasureUnit: String,
    val meterDesc: String? = null,
    @ColumnInfo(index = true) val metersId: UUID,
) {
    companion object {
        const val TABLE_NAME = "meters_tl"

        fun defaultMeterTl(
            meterTlId: UUID = UUID.randomUUID(), meterId: UUID,
            measureUnit: String, meterDesc: String? = null
        ) =
            MeterTlEntity(
                meterTlId = meterTlId,
                meterMeasureUnit = measureUnit,
                meterDesc = meterDesc,
                metersId = meterId
            )

        fun electricityMeterTl(ctx: Context, meterId: UUID) = defaultMeterTl(
            measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.kWh_unit),
            meterId = meterId
        )

        fun gasMeterTl(ctx: Context, meterId: UUID) = defaultMeterTl(
            measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
            meterId = meterId
        )

        fun coldWaterMeterTl(ctx: Context, meterId: UUID) = defaultMeterTl(
            measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
            meterId = meterId
        )

        fun hotWaterMeterTl(ctx: Context, meterId: UUID) = defaultMeterTl(
            measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
            meterId = meterId
        )

        fun heatingMeterTl(ctx: Context, meterId: UUID) = defaultMeterTl(
            measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.Gcalm2_unit),
            meterId = meterId
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MeterTlEntity
        if (meterTlId != other.meterTlId) return false

        return true
    }

    override fun hashCode(): Int {
        return meterTlId.hashCode()
    }
}


