package com.oborodulin.home.servicing.ui.model.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.servicing.domain.model.Service
import com.oborodulin.home.servicing.ui.model.ServiceSubtotalListItem
import java.util.*

class ServiceToServiceSubtotalListItemMapper : Mapper<Service, ServiceSubtotalListItem> {
    override fun map(input: Service) =
        ServiceSubtotalListItem(
            id = input.id ?: UUID.randomUUID(),
            name = input.name,
            type = input.type,
            measureUnit = input.measureUnit,
            serviceDescr = input.descr,
            isPrivileges = input.isPrivileges,
            isAllocateRate = input.isAllocateRate,
            fromPaymentDate = input.fromPaymentDate,
            toPaymentDate = input.toPaymentDate,
            rateValue = input.rateValue,
            diffMeterValue = input.diffMeterValue,
            serviceDebt = input.serviceDebt
        )
}