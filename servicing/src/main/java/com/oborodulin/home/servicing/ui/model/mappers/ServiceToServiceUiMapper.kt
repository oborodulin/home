package com.oborodulin.home.servicing.ui.model.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.servicing.domain.model.Service
import com.oborodulin.home.servicing.ui.model.ServiceUi

class ServiceToServiceUiMapper : Mapper<Service, ServiceUi> {
    override fun map(input: Service) =
        ServiceUi(
            id = input.id,
            servicePos = input.servicePos,
            serviceType = input.serviceType,
            serviceMeterType = input.serviceMeterType,
            serviceTlId = input.serviceTlId,
            serviceName = input.serviceName,
            serviceMeasureUnit = input.serviceMeasureUnit,
            serviceDesc = input.serviceDesc
        )
}