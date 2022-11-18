package com.oborodulin.home.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
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
    }
}