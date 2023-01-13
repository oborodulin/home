package com.oborodulin.home.data.local.db.entities

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oborodulin.home.data.R
import java.math.BigDecimal
import java.util.*

@Entity(tableName = PayerEntity.TABLE_NAME)
class PayerEntity(
    @PrimaryKey var payerId: UUID = UUID.randomUUID(),
    var ercCode: String = "",
    var fullName: String = "",
    var address: String = "",
    var totalArea: BigDecimal? = null,
    var livingSpace: BigDecimal? = null,
    var heatedVolume: BigDecimal? = null,
    var paymentDay: Int? = null,
    var personsNum: Int? = null,
    var isFavorite: Boolean = false,
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
                isFavorite = true
            )
    }
}