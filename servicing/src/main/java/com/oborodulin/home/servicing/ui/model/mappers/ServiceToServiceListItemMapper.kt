package com.oborodulin.home.servicing.ui.model.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.servicing.domain.model.Service
import com.oborodulin.home.servicing.ui.model.ServiceListItem
import java.util.UUID

class ServiceToServiceListItemMapper : Mapper<Service, ServiceListItem> {
    override fun map(input: Service) =
        ServiceListItem(
            id = input.id ?: UUID.randomUUID(),
            servicePos = input.servicePos,
            serviceType = input.serviceType,
            serviceMeterType = input.serviceMeterType,
            serviceName = input.serviceName,
            serviceMeasureUnit = input.serviceMeasureUnit,
            serviceDesc = input.serviceDesc
        )
}