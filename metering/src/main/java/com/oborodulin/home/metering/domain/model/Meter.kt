package com.oborodulin.home.metering.domain.model

import com.oborodulin.home.domain.model.DomainModel
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

data class Meter(
    var payersId: UUID,
    var num: String,
    var maxValue: BigDecimal,
    var passportDate: OffsetDateTime?,
    var verificationPeriod: Int?,
    var tl: MeterTl,
    var meterValues: List<MeterValue>? = listOf(),
    var meterVerifications: List<MeterVerification>? = listOf(),
) : DomainModel()
