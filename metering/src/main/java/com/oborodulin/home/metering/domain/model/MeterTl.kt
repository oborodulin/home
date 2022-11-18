package com.oborodulin.home.metering.domain.model

import com.oborodulin.home.domain.model.DomainModel

data class MeterTl(
    var measureUnit: String,
    var descr: String?,
) : DomainModel()