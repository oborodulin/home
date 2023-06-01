package com.oborodulin.home.domain.model

import com.oborodulin.home.common.domain.model.DomainModel
import java.time.OffsetDateTime

data class Receipt(
    var receiptDate: OffsetDateTime = OffsetDateTime.now(),
    var isPaid: Boolean = false
) : DomainModel()
