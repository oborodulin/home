package com.oborodulin.home.domain.entity

import java.util.*

data class Receipt(
    val id: UUID = UUID.randomUUID(),
    var receiptDate: Date = Date(),
    var isPaid: Boolean = false
)
