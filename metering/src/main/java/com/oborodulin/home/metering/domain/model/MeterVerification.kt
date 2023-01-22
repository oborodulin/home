package com.oborodulin.home.metering.domain.model

import com.oborodulin.home.domain.model.DomainModel
import java.math.BigDecimal
import java.time.OffsetDateTime

data class MeterVerification(
    val startDate: OffsetDateTime = OffsetDateTime.now(),
    val endDate: OffsetDateTime?,
    val startMeterValue: BigDecimal,
    val endMeterValue: BigDecimal?,
    val isOk: Boolean = false,
) : DomainModel()
