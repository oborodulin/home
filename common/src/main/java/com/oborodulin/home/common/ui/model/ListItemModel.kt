package com.oborodulin.home.common.ui.model

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

open class ListItemModel(
    var itemId: UUID,
    val title: String,
    val descr: String? = null,
    val value: BigDecimal? = null,
    val fromDate: OffsetDateTime? = null,
    val toDate: OffsetDateTime? = null,
    val isFavoriteMark: Boolean = false,
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ListItemModel
        if (itemId != other.itemId) return false

        return true
    }

    override fun hashCode(): Int {
        return itemId.hashCode()
    }
}