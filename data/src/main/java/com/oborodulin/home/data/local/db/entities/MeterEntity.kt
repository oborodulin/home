package com.oborodulin.home.data.local.db.entities

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.oborodulin.home.common.data.entities.BaseEntity
import com.oborodulin.home.data.R
import com.oborodulin.home.data.util.MeterType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

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
    @ColumnInfo(index = true) val payersId: UUID
) : BaseEntity() {

    companion object {
        const val TABLE_NAME = "meters"

        //Utils.toOffsetDateTime("2022-06-19T14:29:10.212+03:00")
        val DEF_ELECTRO_INIT_VAL: BigDecimal? = null
        val DEF_GAS_INIT_VAL: BigDecimal? = BigDecimal("0.695")
        val DEF_COLD_WATER_INIT_VAL: BigDecimal? = BigDecimal("9976.125")
        val DEF_HOT_WATER_INIT_VAL: BigDecimal? = BigDecimal("9991.755")
        val DEF_HEATING_INIT_VAL: BigDecimal? = BigDecimal("0.02113")

        val DEF_ELECTRO_MAX_VAL: BigDecimal = BigDecimal("9999")
        val DEF_WATER_MAX_VAL: BigDecimal = BigDecimal("9999.999")
        val DEF_HEATING_MAX_VAL: BigDecimal = BigDecimal("9999.99999")

        fun defaultMeter(
            payerId: UUID = UUID.randomUUID(), meterId: UUID = UUID.randomUUID(),
            meterNum: String = "",
            meterType: MeterType = MeterType.NONE,
            maxValue: BigDecimal = DEF_WATER_MAX_VAL,
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
            ctx: Context, payerId: UUID, currDate: OffsetDateTime? = null,
            initValue: BigDecimal? = DEF_ELECTRO_INIT_VAL
        ) = defaultMeter(
            meterNum = ctx.resources.getString(R.string.def_meter_num),
            payerId = payerId,
            meterType = MeterType.ELECTRICITY,
            maxValue = DEF_ELECTRO_MAX_VAL,
            passportDate = currDate?.minusMonths(7),
            initValue = initValue
        )

        fun gasMeter(
            ctx: Context, payerId: UUID, currDate: OffsetDateTime? = null,
            initValue: BigDecimal? = DEF_GAS_INIT_VAL
        ) = defaultMeter(
            meterNum = ctx.resources.getString(R.string.def_meter_num),
            payerId = payerId,
            meterType = MeterType.GAS,
            maxValue = BigDecimal("99999.999"),
            passportDate = currDate?.minusMonths(7),
            initValue = initValue
        )

        fun coldWaterMeter(
            ctx: Context, payerId: UUID, currDate: OffsetDateTime? = null,
            initValue: BigDecimal? = DEF_COLD_WATER_INIT_VAL,
            meterId: UUID = UUID.randomUUID()
        ) = defaultMeter(
            meterId = meterId,
            meterNum = ctx.resources.getString(R.string.def_meter_num),
            payerId = payerId,
            meterType = MeterType.COLD_WATER,
            maxValue = DEF_WATER_MAX_VAL,
            passportDate = currDate?.minusMonths(7),
            initValue = initValue
        )

        fun heatingMeter(
            ctx: Context, payerId: UUID, currDate: OffsetDateTime? = null,
            initValue: BigDecimal? = DEF_HEATING_INIT_VAL
        ) = defaultMeter(
            meterNum = ctx.resources.getString(R.string.def_meter_num),
            payerId = payerId,
            meterType = MeterType.HEATING,
            maxValue = DEF_HEATING_MAX_VAL,
            passportDate = currDate?.minusMonths(7),
            initValue = initValue
        )

        fun hotWaterMeter(
            ctx: Context, payerId: UUID, currDate: OffsetDateTime? = null,
            initValue: BigDecimal? = DEF_HOT_WATER_INIT_VAL
        ) = defaultMeter(
            meterNum = ctx.resources.getString(R.string.def_meter_num),
            payerId = payerId,
            meterType = MeterType.HOT_WATER,
            maxValue = DEF_WATER_MAX_VAL,
            passportDate = currDate?.minusMonths(7),
            initValue = initValue
        )

    }

    override fun id() = this.meterId

    override fun key(): Int {
        var result = payersId.hashCode()
        result = result * 31 + meterNum.hashCode()
        result = result * 31 + meterType.hashCode()
        passportDate?.let { result = result * 31 + passportDate.hashCode() }
        return result
    }

    override fun toString(): String {
        val str = StringBuffer()
        str.append("Meter Entity '").append(meterType).append("' №").append(meterNum)
            .append(" with max Value = '").append(maxValue).append("'")
        passportDate?.let {
            str.append(". Passport Date ").append(DateTimeFormatter.ISO_LOCAL_DATE.format(it))
                .append(" with")
        }
        initValue?.let { str.append(" init Value = ").append(it) }
        str.append(" [payerId = ").append(payersId)
            .append("] meterId = ").append(meterId)
        return str.toString()
    }
}