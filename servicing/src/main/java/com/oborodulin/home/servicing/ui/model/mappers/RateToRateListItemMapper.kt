package com.oborodulin.home.servicing.ui.model.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.servicing.domain.model.Rate
import com.oborodulin.home.servicing.ui.model.RateListItem
import java.util.UUID

class RateToRateListItemMapper : Mapper<Rate, RateListItem> {
    override fun map(input: Rate) =
        RateListItem(
            id = input.id ?: UUID.randomUUID(),
            serviceId = input.serviceId,
            payerServiceId = input.payerServiceId,
            startDate = input.startDate,
            fromMeterValue = input.fromMeterValue,
            toMeterValue = input.toMeterValue,
            rateValue = input.rateValue,
            isPerPerson = input.isPerPerson,
            isPrivileges = input.isPrivileges
        )
}