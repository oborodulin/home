package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.data.local.db.entities.ServiceTlEntity
import com.oborodulin.home.data.local.db.views.ServiceView
import com.oborodulin.home.servicing.domain.model.Service
import java.util.UUID

class ServiceToServiceViewMapper : Mapper<Service, ServiceView> {
    override fun map(input: Service): ServiceView {
        if (input.id == null) input.apply { id = UUID.randomUUID() }
        return ServiceView(
            data = ServiceEntity(
                serviceId = input.id!!,
                servicePos = input.servicePos,
                serviceType = input.serviceType,
                serviceMeterType = input.serviceMeterType
            ),
            tl = ServiceTlEntity(
                serviceTlId = input.serviceTlId ?: input.apply {
                    serviceTlId = UUID.randomUUID()
                }.serviceTlId!!,
                serviceName = input.serviceName,
                serviceMeasureUnit = input.serviceMeasureUnit,
                serviceDesc = input.serviceDesc,
                servicesId = input.id!!,
            )
        )
    }
}