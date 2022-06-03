package com.oborodulin.home.domain.payer

import androidx.room.Entity
import com.oborodulin.home.domain.BaseEntity
import java.math.BigDecimal

@Entity(tableName = "payers")
class Payer(
    var ercCode: String = "",
    var fullName: String = "",
    var address: String = "",
    var totalArea: BigDecimal? = null,
    var livingSpace: BigDecimal? = null,
    var heatedVolume: BigDecimal? = null,
    var paymentDay: Int? = null,
    var personsNum: Int? = null,
): BaseEntity()
