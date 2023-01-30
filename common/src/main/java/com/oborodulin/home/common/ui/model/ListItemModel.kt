package com.oborodulin.home.common.ui.model

import java.util.*

open class ListItemModel(
    var itemId: UUID,
    val title: String,
    val descr: String? = null,
    val isFavoriteMark: Boolean = false,
)