package com.oborodulin.home.metering.ui.model

import com.oborodulin.home.data.util.ServiceType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

data class MeterValueListItem(
    val id: UUID? = null,
    val payerId: UUID? = null,
    val metersId: UUID,
    val type: ServiceType? = null,
    val name: String = "",
    val measureUnit: String? = null,
    val prevLastDate: OffsetDateTime? = null,
    val prevValue: BigDecimal? = null,
    val valueFormat: String = "#0.000",
    val valueDate: OffsetDateTime = OffsetDateTime.now(),
    val currentValue: BigDecimal? = null
)
