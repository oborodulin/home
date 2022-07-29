package com.oborodulin.home.domain.receipt

import java.util.*

data class Receipt(
    val id: UUID = UUID.randomUUID(),
    var receiptDate: Date = Date(),
    var isPaid: Boolean = false
)
