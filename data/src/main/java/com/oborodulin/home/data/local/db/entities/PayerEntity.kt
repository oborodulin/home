package com.oborodulin.home.data.local.db.entities

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oborodulin.home.data.R
import com.oborodulin.home.data.util.Constants.DEF_PAYMENT_DAY
import java.math.BigDecimal
import java.util.*

@Entity(tableName = PayerEntity.TABLE_NAME)
class PayerEntity(
    @PrimaryKey val payerId: UUID = UUID.randomUUID(),
    val ercCode: String = "",
    val fullName: String = "",
    val address: String = "",
    val totalArea: BigDecimal? = null,
    val livingSpace: BigDecimal? = null,
    val heatedVolume: BigDecimal? = null,
    val paymentDay: Int = DEF_PAYMENT_DAY,
    val personsNum: Int = 1,
    val isAlignByPaymentDay: Boolean = false,
    val isFavorite: Boolean = false,
) {
    companion object {
        const val TABLE_NAME = "payers"

        fun populatePayer1(ctx: Context) =
            PayerEntity(
                ercCode = ctx.resources.getString(R.string.def_payer1_erc_code),
                fullName = ctx.resources.getString(R.string.def_payer1_full_name),
                address = ctx.resources.getString(R.string.def_payer1_address),
                paymentDay = 20,
                totalArea = BigDecimal.valueOf(52.5),
                livingSpace = BigDecimal.valueOf(48.7),
                heatedVolume = BigDecimal.valueOf(121.75),
                personsNum = 2
            )

        fun populatePayer2(ctx: Context) =
            PayerEntity(
                ercCode = ctx.resources.getString(R.string.def_payer2_erc_code),
                fullName = ctx.resources.getString(R.string.def_payer2_full_name),
                address = ctx.resources.getString(R.string.def_payer2_address),
                totalArea = BigDecimal.valueOf(52.5),
                livingSpace = BigDecimal.valueOf(48.7),
                heatedVolume = BigDecimal.valueOf(121.75),
                personsNum = 1,
                isAlignByPaymentDay = true,
                isFavorite = true
            )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PayerEntity
        if (payerId != other.payerId) return false

        return true
    }

    override fun hashCode(): Int {
        return payerId.hashCode()
    }
}