package com.oborodulin.home.servicing.ui.model.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.servicing.domain.model.Rate
import com.oborodulin.home.servicing.ui.model.RateUi

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