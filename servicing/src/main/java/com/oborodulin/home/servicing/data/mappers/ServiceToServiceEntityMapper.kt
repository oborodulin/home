package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.servicing.domain.model.Service
import java.util.*

class ServiceToServiceEntityMapper : Mapper<Service, ServiceEntity> {
    override fun map(input: Service) = ServiceEntity(
        serviceId = input.id ?: input.apply { id = UUID.randomUUID() }.id!!,
        servicePos = input.servicePos,
        serviceType = input.serviceType,
        serviceMeterType = input.meterType
    )
}