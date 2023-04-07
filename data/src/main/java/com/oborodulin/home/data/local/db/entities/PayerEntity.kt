package com.oborodulin.home.data.local.db.entities

import android.content.Context
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.oborodulin.home.data.R
import com.oborodulin.home.data.util.Constants
import com.oborodulin.home.data.util.MeterType
import java.math.BigDecimal
import java.util.*

@Entity(tableName = PayerEntity.TABLE_NAME, indices = [Index(value = ["ercCode"], unique = true)])
class PayerEntity(
    @PrimaryKey val payerId: UUID = UUID.randomUUID(),
    val ercCode: String = "",
    val fullName: String = "",
    val address: String = "",
    val totalArea: BigDecimal? = null,
    val livingSpace: BigDecimal? = null,
    val heatedVolume: BigDecimal? = null,
    val paymentDay: Int = Constants.DEF_PAYMENT_DAY,
    val personsNum: Int = Constants.DEF_PERSON_NUM,
    val isAlignByPaymentDay: Boolean = false,
    val isFavorite: Boolean = false,
) {
    companion object {
        const val TABLE_NAME = "payers"

        fun defaultPayer(
            payerId: UUID = UUID.randomUUID(),
            ercCode: String = "",
            fullName: String = "",
            address: String = "",
            totalArea: BigDecimal? = null,
            livingSpace: BigDecimal? = null,
            heatedVolume: BigDecimal? = null,
            paymentDay: Int = Constants.DEF_PAYMENT_DAY,
            personsNum: Int = Constants.DEF_PERSON_NUM,
            isAlignByPaymentDay: Boolean = false,
            isFavorite: Boolean = false
        ) = PayerEntity(
            payerId = payerId,
            ercCode = ercCode,
            fullName = fullName,
            address = address,
            totalArea = totalArea,
            livingSpace = livingSpace,
            heatedVolume = heatedVolume,
            paymentDay = paymentDay,
            personsNum = personsNum,
            isAlignByPaymentDay = isAlignByPaymentDay,
            isFavorite = isFavorite
        )

        fun payerWithTwoPersons(ctx: Context, payerId: UUID = UUID.randomUUID()) =
            defaultPayer(
                payerId = payerId,
                ercCode = ctx.resources.getString(R.string.def_payer1_erc_code),
                fullName = ctx.resources.getString(R.string.def_payer1_full_name),
                address = ctx.resources.getString(R.string.def_payer1_address),
                paymentDay = Constants.DEF_PAYMENT_DAY,
                totalArea = BigDecimal("53.5"),
                livingSpace = BigDecimal("49.7"),
                heatedVolume = BigDecimal("122.75"),
                personsNum = Constants.DEF_TWO_PERSON_NUM
            )

        fun favoritePayer(
            ctx: Context, payerId: UUID = UUID.randomUUID(),
            livingSpace: BigDecimal? = BigDecimal("48.7")
        ) =
            defaultPayer(
                payerId = payerId,
                ercCode = ctx.resources.getString(R.string.def_payer2_erc_code),
                fullName = ctx.resources.getString(R.string.def_payer2_full_name),
                address = ctx.resources.getString(R.string.def_payer2_address),
                totalArea = BigDecimal("52.5"),
                livingSpace = livingSpace,
                heatedVolume = BigDecimal("121.75"),
                personsNum = Constants.DEF_PERSON_NUM,
                isFavorite = true
            )

        fun payerWithAlignByPaymentDay(ctx: Context, payerId: UUID = UUID.randomUUID()) =
            defaultPayer(
                payerId = payerId,
                ercCode = ctx.resources.getString(R.string.def_payer3_erc_code),
                fullName = ctx.resources.getString(R.string.def_payer3_full_name),
                address = ctx.resources.getString(R.string.def_payer3_address),
                totalArea = BigDecimal("51.5"),
                livingSpace = BigDecimal("47.7"),
                heatedVolume = BigDecimal("120.75"),
                isAlignByPaymentDay = true
            )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PayerEntity
        if (payerId != other.payerId || ercCode != other.ercCode) return false

        return true
    }

    override fun hashCode(): Int {
        return payerId.hashCode()
    }

    override fun toString(): String {
        val str = StringBuffer()
        str.append("Payer ").append(ercCode).append(" '").append(fullName)
            .append("' at address '").append(address).append("' for ").append(personsNum)
            .append(" person:")
        totalArea?.let { str.append(" totalArea: ").append(it).append(";") }
        livingSpace?.let { str.append(" livingSpace: ").append(it).append(";") }
        heatedVolume?.let { str.append(" heatedVolume: ").append(it) }
        str.append(" [paymentDay = ").append(paymentDay)
            .append("; isFavorite = ").append(isFavorite)
            .append("; isAlignByPaymentDay = ").append(isAlignByPaymentDay)
            .append("] payerId = ").append(payerId)
        return str.toString()
    }
}