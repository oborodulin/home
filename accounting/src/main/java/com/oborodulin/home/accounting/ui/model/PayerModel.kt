package com.oborodulin.home.accounting.ui.model

import com.oborodulin.home.data.util.Constants
import java.math.BigDecimal
import java.util.*

data class PayerModel(
    var id: UUID? = null,
    var ercCode: String = "",
    var fullName: String = "",
    var address: String = "",
    var totalArea: BigDecimal? = null,
    var livingSpace: BigDecimal? = null,
    var heatedVolume: BigDecimal? = null,
    var paymentDay: Int = Constants.DEF_PAYMENT_DAY,
    var personsNum: Int = 1,
    var isFavorite: Boolean = false,
//    val isLoading: Boolean,
//    val error: String? = null
)