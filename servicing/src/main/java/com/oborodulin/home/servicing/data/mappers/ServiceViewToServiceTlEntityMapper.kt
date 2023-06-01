package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.entities.ServiceTlEntity
import com.oborodulin.home.data.local.db.views.ServiceView

class ServiceViewToServiceTlEntityMapper : Mapper<ServiceView, ServiceTlEntity> {
    override fun map(input: ServiceView) = input.tl
}