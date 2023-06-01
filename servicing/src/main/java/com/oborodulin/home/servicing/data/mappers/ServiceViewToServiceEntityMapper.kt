package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.data.local.db.views.ServiceView

class ServiceViewToServiceEntityMapper : Mapper<ServiceView, ServiceEntity> {
    override fun map(input: ServiceView) = input.data
}