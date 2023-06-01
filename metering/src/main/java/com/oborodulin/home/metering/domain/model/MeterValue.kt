package com.oborodulin.home.metering.domain.model

import com.oborodulin.home.common.domain.model.DomainModel
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

data class MeterValue(
    val metersId: UUID,
    val valueDate: OffsetDateTime = OffsetDateTime.now(),
    val meterValue: BigDecimal? = null,
) : DomainModel()
