package com.oborodulin.home.metering.domain.model

import java.util.*
import com.oborodulin.home.common.domain.model.DomainModel

data class MeterValue(
    val startDate: Date = Date(),
    val endDate: Date?,
    val isOk: Boolean = false,
) : DomainModel()
