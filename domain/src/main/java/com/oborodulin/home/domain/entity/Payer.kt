package com.oborodulin.home.domain.entity

import androidx.room.Entity
import com.oborodulin.home.domain.entity.BaseEntity
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
