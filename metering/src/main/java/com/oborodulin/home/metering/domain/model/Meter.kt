package com.oborodulin.home.metering.domain.model

import com.oborodulin.home.domain.model.DomainModel
import java.math.BigDecimal
import java.util.*

data class Meter(
    var payersServicesId: UUID,
    var num: String,
    var maxValue: BigDecimal,
    var passportDate: Date?,
    var verificationPeriod: Int?,
    var tl: MeterTl,
    var meterValues: List<MeterValue>? = listOf(),
    var meterVerifications: List<MeterVerification>? = listOf(),
) : DomainModel()
