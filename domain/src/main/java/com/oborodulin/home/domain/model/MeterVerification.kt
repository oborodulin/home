package com.oborodulin.home.domain.model

import java.util.*
import com.oborodulin.home.common.domain.model.DomainModel

data class MeterVerification(
    var meter: Meter,
    val startDate: Date,
    val endDate: Date?,
    val isOk: Boolean = true,
) : DomainModel()
