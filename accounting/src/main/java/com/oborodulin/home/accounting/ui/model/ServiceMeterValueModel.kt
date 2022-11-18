package com.oborodulin.home.accounting.ui.model

import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.util.*

data class ServiceMeterValueModel(
    var type: ServiceType,
    val name: String,
    var meterId: UUID,
    var measureUnit: String?,
    val prevLastDate: Date,
    val prevValue: BigDecimal,
    val currValue: BigDecimal? = null,
)
