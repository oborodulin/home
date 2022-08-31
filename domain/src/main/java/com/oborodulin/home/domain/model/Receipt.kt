package com.oborodulin.home.domain.model

import java.util.*

data class Receipt(
    val id: UUID? = null,
    var receiptDate: Date = Date(),
    var isPaid: Boolean = false
): DomainModel()
