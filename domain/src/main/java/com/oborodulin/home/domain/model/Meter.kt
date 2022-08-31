package com.oborodulin.home.domain.model

import java.math.BigDecimal
import java.util.*

data class Meter(
    var id: UUID? = null,
    var num: String,
    val maxValue: BigDecimal,
    var measureUnit: String,
    val passportDate: Date?,
    val verificationPeriod: Int?,
    var desc: String?,
    var service: Service?,
    var verifications: List<Verification>?
) : DomainModel()