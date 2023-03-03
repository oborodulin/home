package com.oborodulin.home.accounting.ui.model

import com.oborodulin.home.common.ui.model.ListItemModel
import com.oborodulin.home.data.util.Constants.DEF_PAYMENT_DAY
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

data class PayerListItem(
    var id: UUID,
    var fullName: String = "",
    var address: String = "",
    var totalArea: BigDecimal? = null,
    var livingSpace: BigDecimal? = null,
    var paymentDay: Int = DEF_PAYMENT_DAY,
    var personsNum: Int = 1,
    var isFavorite: Boolean = false,
    val fromPaymentDate: OffsetDateTime? = null,
    val toPaymentDate: OffsetDateTime? = null,
    val totalDebt: BigDecimal? = null
) : ListItemModel(
    itemId = id,
    title = fullName,
    descr = address,
    value = totalDebt,
    fromDate = fromPaymentDate,
    toDate = toPaymentDate,
    isFavoriteMark = isFavorite
)
