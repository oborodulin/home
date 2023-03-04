package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.views.ServiceView
import com.oborodulin.home.servicing.domain.model.Service

class ServiceViewToServiceMapper : Mapper<ServiceView, Service> {
    override fun map(input: ServiceView): Service {
        val service = Service(
            serviceTlId = input.tl.serviceTlId,
            servicePos = input.data.servicePos!!,
            serviceName = input.tl.serviceName,
            serviceType = input.data.serviceType,
            measureUnit = input.tl.measureUnit,
            serviceDesc = input.tl.serviceDesc
        )
        service.id = input.data.serviceId
        return service
    }
}