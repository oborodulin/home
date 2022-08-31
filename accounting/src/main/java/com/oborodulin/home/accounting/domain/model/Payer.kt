package com.oborodulin.home.accounting.domain.model

import com.oborodulin.home.domain.model.DomainModel
import java.math.BigDecimal
import java.util.*

data class Payer(
    var id: UUID? = null,
    var ercCode: String = "",
    var fullName: String = "",
    var address: String = "",
    var totalArea: BigDecimal? = null,
    var livingSpace: BigDecimal? = null,
    var heatedVolume: BigDecimal? = null,
    var paymentDay: Int? = null,
    var personsNum: Int? = null,
) : DomainModel()
