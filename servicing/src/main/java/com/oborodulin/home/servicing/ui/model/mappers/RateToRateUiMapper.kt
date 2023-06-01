package com.oborodulin.home.servicing.ui.model.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.servicing.domain.model.Rate
import com.oborodulin.home.servicing.ui.model.RateUi

class RateToRateUiMapper : Mapper<Rate, RateUi> {
    override fun map(input: Rate) = RateUi(
        id = input.id,
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