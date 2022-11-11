package com.oborodulin.home.domain.model

import java.util.*
import com.oborodulin.home.common.domain.model.DomainModel

data class Receipt(
    var receiptDate: Date = Date(),
    var isPaid: Boolean = false
) : DomainModel()
