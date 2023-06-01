package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.servicing.domain.model.Rate
import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.entities.RateEntity

class RateEntityToRateMapper : Mapper<RateEntity, com.oborodulin.home.servicing.domain.model.Rate> {
    override fun map(input: RateEntity): com.oborodulin.home.servicing.domain.model.Rate {
        val rate = com.oborodulin.home.servicing.domain.model.Rate(
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