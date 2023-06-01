package com.oborodulin.home.billing.domain.model

import com.oborodulin.home.common.domain.model.DomainModel
import com.oborodulin.home.servicing.domain.model.PayerService
import java.math.BigDecimal
import java.time.OffsetDateTime

data class PayerServiceSubtotal(
    val payerService: PayerService,
    val fromPaymentDate: OffsetDateTime? = null,
    val toPaymentDate: OffsetDateTime? = null,
    val diffMeterValue: BigDecimal? = null,
    val serviceDebt: BigDecimal = BigDecimal.ZERO
) : DomainModel()
