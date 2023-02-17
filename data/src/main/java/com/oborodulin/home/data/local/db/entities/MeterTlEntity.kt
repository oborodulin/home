package com.oborodulin.home.data.local.db.entities

import android.content.Context
import androidx.room.*
import java.util.*

@Entity(
    tableName = MeterTlEntity.TABLE_NAME,
    indices = [Index(value = ["localeCode", "metersId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = MeterEntity::class,
        parentColumns = arrayOf("meterId"),
        childColumns = arrayOf("metersId"),
        onDelete = ForeignKey.CASCADE,
        deferred = true
    )]
)
data class MeterTlEntity(
    @PrimaryKey var meterTlId: UUID = UUID.randomUUID(),
    val localeCode: String = Locale.getDefault().language,
    val measureUnit: String,
    val descr: String? = null,
    @ColumnInfo(index = true) var metersId: UUID,
) {
    companion object {
        const val TABLE_NAME = "meters_tl"

        fun populateElectricityMeterTl(ctx: Context, meterId: UUID) = MeterTlEntity(
            measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.kWh_unit),
            metersId = meterId
        )

        fun populateColdWaterMeterTl(ctx: Context, meterId: UUID) = MeterTlEntity(
            measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
            metersId = meterId
        )

        fun populateHotWaterMeterTl(ctx: Context, meterId: UUID) = MeterTlEntity(
            measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
            metersId = meterId
        )

        fun populateHeatingMeterTl(ctx: Context, meterId: UUID) = MeterTlEntity(
            measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.Gcalm2_unit),
            metersId = meterId
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


