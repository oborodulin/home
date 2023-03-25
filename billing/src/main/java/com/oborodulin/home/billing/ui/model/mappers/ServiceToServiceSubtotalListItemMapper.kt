package com.oborodulin.home.billing.ui.model.mappers

import com.oborodulin.home.billing.ui.model.ServiceSubtotalListItem
import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.servicing.domain.model.Service
import java.util.*

class ServiceToServiceSubtotalListItemMapper : Mapper<Service, ServiceSubtotalListItem> {
    override fun map(input: Service) = ServiceSubtotalListItem(
        id = input.id ?: UUID.randomUUID(),
        serviceName = input.serviceName,
        serviceType = input.serviceType,
        serviceMeasureUnit = input.serviceMeasureUnit,
        serviceDesc = input.serviceDesc,
        isPrivileges = input.isPrivileges,
        isAllocateRate = input.isAllocateRate,
        fromPaymentDate = input.fromPaymentDate,
        toPaymentDate = input.toPaymentDate,
        diffMeterValue = input.diffMeterValue,
        serviceDebt = input.serviceDebt
    )
}