package com.oborodulin.home.billing.ui.model

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class RateUi(
    val id: UUID? = null,
    val serviceId: UUID,
    val payerServiceId: UUID? = null,
    val startDate: OffsetDateTime = OffsetDateTime.now(),
    val fromMeterValue: BigDecimal? = null,
    val toMeterValue: BigDecimal? = null,
    val rateValue: BigDecimal,
    val isPerPerson: Boolean = false,
    val isPrivileges: Boolean = false
)