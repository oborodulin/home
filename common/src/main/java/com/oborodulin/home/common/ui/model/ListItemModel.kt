package com.oborodulin.home.common.ui.model

import android.content.Context
import com.oborodulin.home.common.R
import com.oborodulin.home.common.util.Utils
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
) {
    companion object {
        const val TABLE_NAME = "payers"

        fun defaultListItemModel(ctx: Context) = ListItemModel(
            itemId = UUID.randomUUID(),
            title = ctx.resources.getString(R.string.preview_blank_title),
            descr = ctx.resources.getString(R.string.preview_blank_descr),
            value = BigDecimal("123456.54"),
            fromDate = Utils.toOffsetDateTime("2022-08-01T14:29:10.212+03:00"),
            toDate = Utils.toOffsetDateTime("2022-09-01T14:29:10.212+03:00")
        )
    }

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