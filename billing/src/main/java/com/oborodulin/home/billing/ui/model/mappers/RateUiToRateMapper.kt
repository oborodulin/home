package com.oborodulin.home.billing.ui.model.mappers

import com.oborodulin.home.billing.domain.model.Rate
import com.oborodulin.home.billing.ui.model.RateUi
import com.oborodulin.home.common.mapping.Mapper

class RateUiToRateMapper : Mapper<RateUi, Rate> {
    override fun map(input: RateUi): Rate {
        val rate = Rate(
            serviceId = input.serviceId,
            payerServiceId = input.payerServiceId,
            startDate = input.startDate,
            fromMeterValue = input.fromMeterValue,
            toMeterValue = input.toMeterValue,
            rateValue = input.rateValue,
            isPerPerson = input.isPerPerson,
            isPrivileges = input.isPrivileges
        )
        rate.id = input.id
        return rate
    }
}