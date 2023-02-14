package com.oborodulin.home.metering.ui.model

import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

data class MeterValueListItem(
    var id: UUID? = null,
    var payerId: UUID? = null,
    var metersId: UUID,
    var type: ServiceType? = null,
    val name: String = "",
    var measureUnit: String? = null,
    val prevLastDate: OffsetDateTime? = null,
    val prevValue: BigDecimal? = null,
    val valueFormat: String = "#0.000",
    val valueDate: OffsetDateTime = OffsetDateTime.now(),
    val currentValue: BigDecimal? = null
)
