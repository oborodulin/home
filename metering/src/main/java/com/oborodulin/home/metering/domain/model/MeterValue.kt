package com.oborodulin.home.metering.domain.model

import com.oborodulin.home.domain.model.DomainModel
import java.math.BigDecimal
import java.util.*

data class MeterValue(
    val metersId: UUID,
    val valueDate: Date = Date(),
    val meterValue: BigDecimal? = null,
) : DomainModel()
