package com.oborodulin.home.metering.domain.model

import com.oborodulin.home.domain.model.DomainModel
import java.time.OffsetDateTime

data class MeterVerification(
    val startDate: OffsetDateTime = OffsetDateTime.now(),
    val endDate: OffsetDateTime?,
    val isOk: Boolean = false,
) : DomainModel()
