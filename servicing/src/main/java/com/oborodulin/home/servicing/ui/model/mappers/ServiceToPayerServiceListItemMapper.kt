package com.oborodulin.home.servicing.ui.model.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.servicing.domain.model.Service
import com.oborodulin.home.servicing.ui.model.PayerServiceListItem
import java.util.UUID

class ServiceToPayerServiceListItemMapper : Mapper<Service, PayerServiceListItem> {
    override fun map(input: Service) =
        PayerServiceListItem(
            id = input.id ?: UUID.randomUUID(),
            servicePos = input.servicePos,
            serviceType = input.serviceType,
            serviceMeterType = input.serviceMeterType,
            serviceName = input.serviceName,
            serviceMeasureUnit = input.serviceMeasureUnit,
            serviceDesc = input.serviceDesc,
            fromMonth = input.fromMonth,
            fromYear = input.fromYear,
            periodFromDate = input.periodFromDate,
            periodToDate = input.periodToDate,
            isMeterOwner = input.isMeterOwner ?: false,
            isPrivileges = input.isPrivileges ?: false,
            isAllocateRate = input.isAllocateRate ?: false
        )
}