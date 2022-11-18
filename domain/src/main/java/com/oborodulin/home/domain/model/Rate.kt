package com.oborodulin.home.domain.model

import java.math.BigDecimal
import java.util.*

class Rate(
    var startDate: Date = Date(),
    var fromMeterValue: BigDecimal? = null,
    var toMeterValue: BigDecimal? = null,
    var rateValue: BigDecimal,
    var service: Service,
) : DomainModel()