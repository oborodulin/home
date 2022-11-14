package com.oborodulin.home.data.local.db.entities.pojo

import java.math.BigDecimal
import java.util.*

data class MeterPojo(
    var id: UUID,
    var payerServicesId: UUID,
    var num: String,
    var maxValue: BigDecimal,
    var passportDate: Date? = null,
    var verificationPeriod: Int? = null,
    var metersTlId: UUID,
    var measureUnit: String? = null,
    val descr: String? = null,
)
