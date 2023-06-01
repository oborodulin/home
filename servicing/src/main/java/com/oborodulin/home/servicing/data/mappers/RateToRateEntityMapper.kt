package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.servicing.domain.model.Rate
import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.entities.RateEntity
import java.util.*

class RateToRateEntityMapper : Mapper<com.oborodulin.home.servicing.domain.model.Rate, RateEntity> {
    override fun map(input: com.oborodulin.home.servicing.domain.model.Rate) = RateEntity(
        rateId = input.id ?: input.apply { id = UUID.randomUUID() }.id!!,
        startDate = input.startDate,
        fromMeterValue = input.fromMeterValue,
        toMeterValue = input.toMeterValue,
        rateValue = input.rateValue,
        isPerPerson = input.isPerPerson,
        isPrivileges = input.isPrivileges,
        servicesId = input.serviceId,
        payersServicesId = input.payerServiceId
    )
}