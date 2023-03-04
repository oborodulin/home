package com.oborodulin.home.billing.ui.model.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.servicing.domain.model.Service
import java.util.*

class ServiceToServiceSubtotalListItemMapper : Mapper<Service, com.oborodulin.home.billing.ui.model.ServiceSubtotalListItem> {
    override fun map(input: Service) =
        com.oborodulin.home.billing.ui.model.ServiceSubtotalListItem(
            id = input.id ?: UUID.randomUUID(),
            serviceName = input.serviceName,
            serviceType = input.serviceType,
            measureUnit = input.measureUnit,
            serviceDesc = input.serviceDesc,
            isPrivileges = input.isPrivileges,
            isAllocateRate = input.isAllocateRate,
            fromPaymentDate = input.fromPaymentDate,
            toPaymentDate = input.toPaymentDate,
            rateValue = input.rateValue,
            diffMeterValue = input.diffMeterValue,
            serviceDebt = input.serviceDebt
        )
}