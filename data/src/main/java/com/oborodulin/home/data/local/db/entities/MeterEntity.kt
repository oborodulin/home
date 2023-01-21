package com.oborodulin.home.data.local.db.entities

import android.content.Context
import androidx.room.*
import com.oborodulin.home.data.R
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@Entity(
    tableName = MeterEntity.TABLE_NAME,
    indices = [Index(value = ["num", "payersServicesId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = PayerServiceCrossRefEntity::class,
        parentColumns = arrayOf("payerServiceId"),
        childColumns = arrayOf("payersServicesId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class MeterEntity(
    @PrimaryKey var meterId: UUID = UUID.randomUUID(),
    var num: String,
    val maxValue: BigDecimal,
    //@TypeConverters(DateTypeConverter::class)
    val passportDate: OffsetDateTime? = null,
    val verificationPeriod: Int? = null,
    @ColumnInfo(index = true) var payersServicesId: UUID,
) {
    companion object {
        const val TABLE_NAME = "meters"

        fun populateElectricityMeter(ctx: Context, payersServiceId: UUID) = MeterEntity(
            num = ctx.resources.getString(R.string.def_meter_num),
            payersServicesId = payersServiceId,
            maxValue = BigDecimal.valueOf(9999)
        )

        fun populateColdWaterMeter(ctx: Context, payersServiceId: UUID) = MeterEntity(
            num = ctx.resources.getString(R.string.def_meter_num),
            payersServicesId = payersServiceId,
            maxValue = BigDecimal.valueOf(99999.999)
        )

        fun populateHotWaterMeter(ctx: Context, payersServiceId: UUID) = MeterEntity(
            num = ctx.resources.getString(R.string.def_meter_num),
            payersServicesId = payersServiceId,
            maxValue = BigDecimal.valueOf(99999.999)
        )

        fun populateHeatingMeter(ctx: Context, payersServiceId: UUID) = MeterEntity(
            num = ctx.resources.getString(R.string.def_meter_num),
            payersServicesId = payersServiceId,
            maxValue = BigDecimal.valueOf(9.99999)
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MeterEntity
        if (meterId != other.meterId) return false

        return true
    }

    override fun hashCode(): Int {
        return meterId.hashCode()
    }
}