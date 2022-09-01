package com.oborodulin.home.domain.model

import java.math.BigDecimal
import java.util.*

data class Meter(
    var num: String,
    val maxValue: BigDecimal,
    var measureUnit: String,
    val passportDate: Date?,
    val verificationPeriod: Int?,
    var descr: String?,
    var service: Service?,
    var meterVerifications: List<MeterVerification>?
) : DomainModel()