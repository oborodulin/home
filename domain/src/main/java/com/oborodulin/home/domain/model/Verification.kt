package com.oborodulin.home.domain.model

import java.util.*

data class Verification(
    var id: UUID? = null,
    val startDate: Date,
    val endDate: Date?,
    val isOk: Boolean = true,
    var meter: Meter?
) : DomainModel()
