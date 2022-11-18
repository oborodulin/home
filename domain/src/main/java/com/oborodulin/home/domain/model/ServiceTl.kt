package com.oborodulin.home.domain.model

import com.oborodulin.home.domain.model.DomainModel

data class ServiceTl(
    var measureUnit: String,
    var descr: String?,
) : DomainModel()