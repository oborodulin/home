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
        onDelete = ForeignKey.CASCADE
    )]
)
data class MeterTlEntity(
    @PrimaryKey var meterTlId: UUID = UUID.randomUUID(),
    val localeCode: String = Locale.getDefault().language,
    var measureUnit: String? = null,
    val descr: String? = null,
    @ColumnInfo(index = true) var metersId: UUID? = null,
) {
    companion object {
        const val TABLE_NAME = "meters_tl"

        fun populateElectricityMeterTl(ctx: Context) = MeterTlEntity(
            measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.kWh_unit)
        )

        fun populateColdWaterMeterTl(ctx: Context) = MeterTlEntity(
            measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit)
        )

        fun populateHotWaterMeterTl(ctx: Context) = MeterTlEntity(
            measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit)
        )
    }
}


