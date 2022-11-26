package com.oborodulin.home.metering.ui.model

import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.util.*

data class MeterValueModel(
    var id: UUID = UUID.randomUUID(),
    var metersId: UUID,
    var type: ServiceType? = null,
    val name: String = "",
    var measureUnit: String? = null,
    val prevLastDate: Date? = null,
    val prevValue: BigDecimal? = null,
    val valueDate: Date = Date(),
    val currentValue: BigDecimal? = null,
)
