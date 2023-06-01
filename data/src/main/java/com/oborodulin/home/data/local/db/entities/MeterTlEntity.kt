package com.oborodulin.home.data.local.db.entities

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.oborodulin.home.common.data.entities.BaseEntity
import com.oborodulin.home.data.util.MeterType
import java.util.Locale
import java.util.UUID

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
) : BaseEntity() {

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

        fun meterTl(ctx: Context, meterType: MeterType, meterId: UUID) =
            when (meterType) {
                MeterType.ELECTRICITY -> electricityMeterTl(ctx, meterId)
                MeterType.GAS -> gasMeterTl(ctx, meterId)
                MeterType.COLD_WATER -> coldWaterMeterTl(ctx, meterId)
                MeterType.HEATING -> heatingMeterTl(ctx, meterId)
                MeterType.HOT_WATER -> hotWaterMeterTl(ctx, meterId)
                MeterType.NONE -> defaultMeterTl(meterId = UUID.randomUUID(), measureUnit = "")
            }
    }

    override fun id() = this.meterTlId
}


