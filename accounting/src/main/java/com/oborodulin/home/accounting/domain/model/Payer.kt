package com.oborodulin.home.accounting.domain.model

import com.oborodulin.home.common.domain.model.ListItemModel
import java.math.BigDecimal

data class Payer(
    var ercCode: String = "",
    var fullName: String = "",
    var address: String = "",
    var totalArea: BigDecimal? = null,
    var livingSpace: BigDecimal? = null,
    var heatedVolume: BigDecimal? = null,
    var paymentDay: Int? = null,
    var personsNum: Int? = null,
    var isFavorite: Boolean = false,
) : ListItemModel(fullName, address)
