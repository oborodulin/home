package com.oborodulin.home.accounting.ui.model

import com.oborodulin.home.common.ui.model.ListItemModel
import com.oborodulin.home.data.util.Constants.DEF_PAYMENT_DAY
import java.math.BigDecimal
import java.util.*

data class PayerListItemModel(
    var id: UUID,
    var fullName: String = "",
    var address: String = "",
    var totalArea: BigDecimal? = null,
    var livingSpace: BigDecimal? = null,
    var paymentDay: Int = DEF_PAYMENT_DAY,
    var personsNum: Int = 1,
    var isFavorite: Boolean = false,
) : ListItemModel(itemId = id, title = fullName, descr = address, isFavoriteMark = isFavorite)
