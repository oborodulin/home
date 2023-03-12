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
    indices = [Index(value = ["payersId", "meterNum", "meterType", "passportDate"], unique = true)],
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
    val meterNum: String = "",
    val meterType: MeterType,
    val maxValue: BigDecimal,
    val passportDate: OffsetDateTime? = null,
    val initValue: BigDecimal? = null,
    val verificationPeriod: Int? = null,
    @ColumnInfo(index = true) val payersId: UUID,
) {
    companion object {
        const val TABLE_NAME = "meters"
        val DEF_PASSPORT_DATE = Utils.toOffsetDateTime("2022-06-19T14:29:10.212")
        val DEF_GAS_INIT_VAL = BigDecimal.valueOf(0.695)

        fun defaultMeter(
            payerId: UUID = UUID.randomUUID(), meterId: UUID = UUID.randomUUID(),
            meterNum: String = "",
            meterType: MeterType = MeterType.NONE,
            maxValue: BigDecimal = BigDecimal.valueOf(9999.999),
            passportDate: OffsetDateTime? = null,
            initValue: BigDecimal? = null,
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

        fun electricityMeter(
            ctx: Context, payerId: UUID,
            passportDate: OffsetDateTime? = null,
            initValue: BigDecimal? = null
        ) = defaultMeter(
            meterNum = ctx.resources.getString(R.string.def_meter_num),
            payerId = payerId,
            meterType = MeterType.ELECTRICITY,
            maxValue = BigDecimal.valueOf(9999),
            passportDate = passportDate,
            initValue = initValue
        )

        fun gasMeter(
            ctx: Context, payerId: UUID,
            passportDate: OffsetDateTime? = DEF_PASSPORT_DATE,
            initValue: BigDecimal? = DEF_GAS_INIT_VAL
        ) = defaultMeter(
            meterNum = ctx.resources.getString(R.string.def_meter_num),
            payerId = payerId,
            meterType = MeterType.GAS,
            maxValue = BigDecimal.valueOf(9999.999),
            passportDate = passportDate,
            initValue = initValue
        )

        fun coldWaterMeter(
            ctx: Context, payerId: UUID,
            passportDate: OffsetDateTime? = DEF_PASSPORT_DATE,
            initValue: BigDecimal? = null
        ) = defaultMeter(
            meterNum = ctx.resources.getString(R.string.def_meter_num),
            payerId = payerId,
            meterType = MeterType.COLD_WATER,
            maxValue = BigDecimal.valueOf(99999.999),
            passportDate = passportDate,
            initValue = initValue ?: BigDecimal.valueOf(1523.125)
        )

        fun hotWaterMeter(
            ctx: Context, payerId: UUID,
            passportDate: OffsetDateTime? = DEF_PASSPORT_DATE,
            initValue: BigDecimal? = null
        ) = defaultMeter(
            meterNum = ctx.resources.getString(R.string.def_meter_num),
            payerId = payerId,
            meterType = MeterType.HOT_WATER,
            maxValue = BigDecimal.valueOf(99999.999),
            passportDate = passportDate,
            initValue = initValue ?: BigDecimal.valueOf(2145.755)
        )

        fun heatingMeter(ctx: Context, payerId: UUID) = defaultMeter(
            meterNum = ctx.resources.getString(R.string.def_meter_num),
            payerId = payerId,
            meterType = MeterType.HEATING,
            maxValue = BigDecimal.valueOf(9.99999),
            passportDate = DEF_PASSPORT_DATE,
            initValue = BigDecimal.valueOf(0.02113)
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