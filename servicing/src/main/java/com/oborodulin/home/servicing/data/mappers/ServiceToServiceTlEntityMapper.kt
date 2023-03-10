package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.entities.ServiceTlEntity
import com.oborodulin.home.servicing.domain.model.Service
import java.util.*

class ServiceToServiceTlEntityMapper : Mapper<Service, ServiceTlEntity> {
    override fun map(input: Service) = ServiceTlEntity(
        serviceTlId = input.serviceTlId ?: input.apply { serviceTlId = UUID.randomUUID() }.serviceTlId!!,
        serviceName = input.serviceName,
        serviceMeasureUnit = input.serviceMeasureUnit,
        serviceDesc = input.serviceDesc,
        servicesId = input.id!!
    )
}