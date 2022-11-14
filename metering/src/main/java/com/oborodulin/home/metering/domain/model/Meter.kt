package com.oborodulin.home.metering.domain.model

import com.oborodulin.home.common.domain.model.DomainModel
import com.oborodulin.home.domain.model.Service
import java.math.BigDecimal
import java.util.*

data class Meter(
    var num: String,
    var maxValue: BigDecimal,
    var passportDate: Date?,
    var verificationPeriod: Int?,
    var tl: MeterTl,
    var meterValues: List<MeterValue>?,
    var meterVerifications: List<MeterVerification>?
) : DomainModel()
