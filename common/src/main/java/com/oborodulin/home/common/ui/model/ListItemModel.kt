package com.oborodulin.home.common.ui.model

import java.util.*

open class ListItemModel(
    var itemId: UUID = UUID.randomUUID(),
    val title: String,
    val descr: String?
)