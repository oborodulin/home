package com.oborodulin.home.domain.model

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

class Rate(
    var startDate: OffsetDateTime = OffsetDateTime.now(),
    var fromMeterValue: BigDecimal? = null,
    var toMeterValue: BigDecimal? = null,
    var rateValue: BigDecimal,
    var service: Service,
) : DomainModel()