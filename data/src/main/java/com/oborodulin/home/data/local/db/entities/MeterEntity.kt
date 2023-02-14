package com.oborodulin.home.data.local.db.entities

import android.content.Context
import androidx.room.*
import com.oborodulin.home.data.R
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@Entity(
    tableName = MeterEntity.TABLE_NAME,
    indices = [Index(value = ["num", "payersId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = PayerEntity::class,
        parentColumns = arrayOf("payerId"),
        childColumns = arrayOf("payersId"),
        onDelete = ForeignKey.CASCADE,
        deferred = true
    )]
)
data class MeterEntity(
    @PrimaryKey var meterId: UUID = UUID.randomUUID(),
    var num: String,
    val maxValue: BigDecimal,
    //@TypeConverters(DateTypeConverter::class)
    val passportDate: OffsetDateTime? = null,
    val verificationPeriod: Int? = null,
    @ColumnInfo(index = true) var payersId: UUID,
) {
    companion object {
        const val TABLE_NAME = "meters"

        fun populateElectricityMeter(ctx: Context, payerId: UUID) = MeterEntity(
            num = ctx.resources.getString(R.string.def_meter_num),
            payersId = payerId,
            maxValue = BigDecimal.valueOf(9999)
        )

        fun populateColdWaterMeter(ctx: Context, payerId: UUID) = MeterEntity(
            num = ctx.resources.getString(R.string.def_meter_num),
            payersId = payerId,
            maxValue = BigDecimal.valueOf(99999.999)
        )

        fun populateHotWaterMeter(ctx: Context, payerId: UUID) = MeterEntity(
            num = ctx.resources.getString(R.string.def_meter_num),
            payersId = payerId,
            maxValue = BigDecimal.valueOf(99999.999)
        )

        fun populateHeatingMeter(ctx: Context, payerId: UUID) = MeterEntity(
            num = ctx.resources.getString(R.string.def_meter_num),
            payersId = payerId,
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