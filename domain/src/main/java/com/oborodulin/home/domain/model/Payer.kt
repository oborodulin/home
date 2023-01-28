package com.oborodulin.home.domain.model

import java.math.BigDecimal

data class Payer(
    var ercCode: String = "",
    var fullName: String = "",
    var address: String = "",
    var totalArea: BigDecimal? = null,
    var livingSpace: BigDecimal? = null,
    var heatedVolume: BigDecimal? = null,
    var paymentDay: Int = 20,
    var personsNum: Int = 1,
    var isFavorite: Boolean = false,
) : DomainModel()
