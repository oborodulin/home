package com.oborodulin.home.billing.domain.model

import com.oborodulin.home.common.domain.model.DomainModel
import java.math.BigDecimal
import java.time.OffsetDateTime

data class PayerDebt(
    var fromPaymentDate: OffsetDateTime? = null,
    var toPaymentDate: OffsetDateTime? = null,
    var totalDebt: BigDecimal = BigDecimal.ZERO
) : DomainModel()
