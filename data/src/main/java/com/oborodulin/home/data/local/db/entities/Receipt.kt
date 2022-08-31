package com.oborodulin.home.data.local.db.entities

import java.util.*

data class Receipt(
    val id: UUID = UUID.randomUUID(),
    var receiptDate: Date = Date(),
    var isPaid: Boolean = false
)
