package com.oborodulin.home.metering.domain.model

import com.oborodulin.home.data.util.MeterType
import com.oborodulin.home.domain.model.DomainModel
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

data class Meter(
    val payersId: UUID,
    val payersServicesId: UUID,
    val meterType: MeterType,
    val meterNum: String,
    val maxValue: BigDecimal,
    val passportDate: OffsetDateTime,
    val initValue: BigDecimal = BigDecimal.ZERO,
    val verificationPeriod: Int?,
    val tl: MeterTl,
    var meterValues: List<MeterValue>? = emptyList(),
    var meterVerifications: List<MeterVerification>? = emptyList(),
) : DomainModel()
