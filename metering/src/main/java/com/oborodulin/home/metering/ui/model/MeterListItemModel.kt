package com.oborodulin.home.metering.ui.model

import com.oborodulin.home.common.ui.model.ListItemModel
import java.math.BigDecimal
import java.util.*

data class MeterListItemModel(
    var id: UUID,
    var fullName: String = "",
    var address: String = "",
    var totalArea: BigDecimal? = null,
    var livingSpace: BigDecimal? = null,
    var paymentDay: Int? = null,
    var personsNum: Int? = null,
    var isFavorite: Boolean = false,
) : ListItemModel(itemId = id, title = fullName, descr = address)
