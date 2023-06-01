package com.oborodulin.home.metering.domain.model

import com.oborodulin.home.common.domain.model.DomainModel

data class MeterTl(
    val measureUnit: String,
    val meterDesc: String?,
) : DomainModel()