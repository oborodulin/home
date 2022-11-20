package com.oborodulin.home.common.ui.model

import java.util.UUID

open class ListItemModel(
    val itemId: UUID,
    val title: String,
    val descr: String?
)