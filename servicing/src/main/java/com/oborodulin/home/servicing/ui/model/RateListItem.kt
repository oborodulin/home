package com.oborodulin.home.servicing.ui.model

import com.oborodulin.home.common.ui.model.ListItemModel
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import java.util.UUID

data class RateListItem(
    val id: UUID,
    val serviceId: UUID,
    val payerServiceId: UUID? = null,
    val startDate: OffsetDateTime,
    val fromMeterValue: BigDecimal? = null,
    val toMeterValue: BigDecimal? = null,
    val rateValue: BigDecimal,
    val isPerPerson: Boolean = false,
    val isPrivileges: Boolean = false
) : ListItemModel(
    itemId = id,
    title = startDate.format(
        DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.getDefault()),
    ),
    descr = if (fromMeterValue != null) "$fromMeterValue - $toMeterValue" else null,
    value = rateValue
)
