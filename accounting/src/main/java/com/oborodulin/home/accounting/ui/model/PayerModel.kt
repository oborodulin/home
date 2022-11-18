package com.oborodulin.home.accounting.ui.model

import java.math.BigDecimal
import java.util.*

data class PayerModel(
    var id: UUID = UUID.randomUUID(),
    var ercCode: String = "",
    var fullName: String = "",
    var address: String = "",
    var totalArea: BigDecimal? = null,
    var livingSpace: BigDecimal? = null,
    var heatedVolume: BigDecimal? = null,
    var paymentDay: Int? = null,
    var personsNum: Int? = null,
    var isFavorite: Boolean = false,
//    val isLoading: Boolean,
//    val error: String? = null
)