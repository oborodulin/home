package com.oborodulin.home.metering.ui.model

import com.oborodulin.home.common.ui.model.ListItemModel
import java.math.BigDecimal
import java.util.*

data class MeterListItem(
    val id: UUID,
    val fullName: String = "",
    val address: String = "",
    val totalArea: BigDecimal? = null,
    val livingSpace: BigDecimal? = null,
    val paymentDay: Int? = null,
    val personsNum: Int? = null,
    val isFavorite: Boolean = false,
) : ListItemModel(itemId = id, title = fullName, descr = address)
