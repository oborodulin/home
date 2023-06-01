package com.oborodulin.home.metering.domain.model

import com.oborodulin.home.data.util.MeterType
import com.oborodulin.home.common.domain.model.DomainModel
import com.oborodulin.home.servicing.domain.model.Service
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

data class Meter(
    val payersId: UUID,
    val meterType: MeterType,
    val meterNum: String,
    val maxValue: BigDecimal,
    val passportDate: OffsetDateTime? = null,
    val initValue: BigDecimal? = null,
    val verificationPeriod: Int? = null,
    val tl: MeterTl,
    var meterValues: List<MeterValue>? = emptyList(),
    var meterVerifications: List<MeterVerification>? = emptyList(),
    var services: List<Service>? = emptyList()
) : DomainModel()
