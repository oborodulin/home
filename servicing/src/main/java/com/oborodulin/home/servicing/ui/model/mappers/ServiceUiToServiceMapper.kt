package com.oborodulin.home.servicing.ui.model.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.servicing.domain.model.Service
import com.oborodulin.home.servicing.ui.model.ServiceUi

class ServiceUiToServiceMapper : Mapper<ServiceUi, Service> {
    override fun map(input: ServiceUi): Service {
        val service = Service(
            servicePos = input.servicePos,
            serviceType = input.serviceType,
            serviceMeterType = input.serviceMeterType,
            serviceTlId = input.serviceTlId,
            serviceName = input.serviceName,
            serviceMeasureUnit = input.serviceMeasureUnit,
            serviceDesc = input.serviceDesc
        )
        service.id = input.id
        return service
    }
}