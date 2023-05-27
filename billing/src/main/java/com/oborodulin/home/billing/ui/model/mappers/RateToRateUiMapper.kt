package com.oborodulin.home.billing.ui.model.mappers

import com.oborodulin.home.billing.domain.model.Rate
import com.oborodulin.home.billing.ui.model.RateUi
import com.oborodulin.home.common.mapping.Mapper

class RateToRateUiMapper : Mapper<Rate, RateUi> {
    override fun map(input: Rate) =
        RateUi(
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