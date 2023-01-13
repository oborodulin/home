package com.oborodulin.home.domain.model

import java.time.OffsetDateTime
import java.util.*

data class Receipt(
    var receiptDate: OffsetDateTime = OffsetDateTime.now(),
    var isPaid: Boolean = false
) : DomainModel()
