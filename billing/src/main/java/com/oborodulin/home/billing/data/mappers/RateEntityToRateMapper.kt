package com.oborodulin.home.billing.data.mappers

import com.oborodulin.home.billing.domain.model.Rate
import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.entities.RateEntity

class RateEntityToRateMapper : Mapper<RateEntity, Rate> {
    override fun map(input: RateEntity): Rate {
        val rate = Rate(
            serviceId = input.servicesId,
            startDate = input.startDate,
            fromMeterValue = input.fromMeterValue,
            toMeterValue = input.toMeterValue,
            rateValue = input.rateValue,
            isPerPerson = input.isPerPerson,
            isPrivileges = input.isPrivileges
        )
        rate.id = input.rateId
        return rate
    }
}