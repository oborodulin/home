package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.views.ServiceView
import com.oborodulin.home.servicing.domain.model.Service

class ServiceViewToServiceMapper : Mapper<ServiceView, Service> {
    override fun map(input: ServiceView): Service {
        val service = Service(
            serviceTlId = input.tl.serviceTlId,
            pos = input.data.pos!!,
            name = input.tl.name,
            type = input.data.type,
            measureUnit = input.tl.measureUnit,
            descr = input.tl.descr
        )
        service.id = input.data.serviceId
        return service
    }
}