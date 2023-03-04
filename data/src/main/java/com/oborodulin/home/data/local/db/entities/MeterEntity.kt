package com.oborodulin.home.data.local.db.entities

import android.content.Context
import androidx.room.*
import com.oborodulin.home.common.util.Utils
import com.oborodulin.home.data.R
import com.oborodulin.home.data.util.MeterType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@Entity(
    tableName = MeterEntity.TABLE_NAME,
    indices = [Index(value = ["payersId", "meterNum", "meterType"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = PayerEntity::class,
        parentColumns = arrayOf("payerId"),
        childColumns = arrayOf("payersId"),
        onDelete = ForeignKey.CASCADE,
        deferred = true
    )]
)
data class MeterEntity(
    @PrimaryKey val meterId: UUID = UUID.randomUUID(),
    val meterNum: String,
    val meterType: MeterType,
    val maxValue: BigDecimal,
    val passportDate: OffsetDateTime,
    val initValue: BigDecimal = BigDecimal.ZERO,
    val verificationPeriod: Int? = null,
    @ColumnInfo(index = true) var payersId: UUID,
) {
    companion object {
        const val TABLE_NAME = "meters"

        fun populateElectricityMeter(ctx: Context, payerId: UUID) = MeterEntity(
            meterNum = ctx.resources.getString(R.string.def_meter_num),
            payersId = payerId,
            meterType = MeterType.ELECTRICITY,
            maxValue = BigDecimal.valueOf(9999),
            passportDate = Utils.toOffsetDateTime("2022-06-19T14:29:10.212"),
            initValue = BigDecimal.valueOf(9344)
        )

        fun populateColdWaterMeter(ctx: Context, payerId: UUID) = MeterEntity(
            meterNum = ctx.resources.getString(R.string.def_meter_num),
            payersId = payerId,
            meterType = MeterType.COLD_WATER,
            maxValue = BigDecimal.valueOf(99999.999),
            passportDate = Utils.toOffsetDateTime("2022-08-11T14:29:10.212"),
            initValue = BigDecimal.valueOf(1523.125)
        )

        fun populateHotWaterMeter(ctx: Context, payerId: UUID) = MeterEntity(
            meterNum = ctx.resources.getString(R.string.def_meter_num),
            payersId = payerId,
            meterType = MeterType.HOT_WATER,
            maxValue = BigDecimal.valueOf(99999.999),
            passportDate = Utils.toOffsetDateTime("2022-09-03T14:29:10.212"),
            initValue = BigDecimal.valueOf(2145.755)
        )

        fun populateHeatingMeter(ctx: Context, payerId: UUID) = MeterEntity(
            meterNum = ctx.resources.getString(R.string.def_meter_num),
            payersId = payerId,
            meterType = MeterType.HEATING,
            maxValue = BigDecimal.valueOf(9.99999),
            passportDate = Utils.toOffsetDateTime("2022-10-21T14:29:10.212"),
            initValue = BigDecimal.valueOf(0.02113)
        )

        fun populateMeter(
            payerId: UUID, meterId: UUID = UUID.randomUUID(),
            meterNum: String,
            meterType: MeterType,
            maxValue: BigDecimal,
            passportDate: OffsetDateTime,
            initValue: BigDecimal = BigDecimal.ZERO,
            verificationPeriod: Int? = null
        ) = MeterEntity(
            payersId = payerId,
            meterId = meterId,
            meterNum = meterNum,
            meterType = meterType,
            maxValue = maxValue,
            passportDate = passportDate,
            initValue = initValue,
            verificationPeriod = verificationPeriod
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