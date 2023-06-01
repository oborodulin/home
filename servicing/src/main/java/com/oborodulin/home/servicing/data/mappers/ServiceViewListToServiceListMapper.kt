package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.ListMapperImpl
import com.oborodulin.home.data.local.db.views.ServiceView
import com.oborodulin.home.servicing.domain.model.Service

class ServiceViewListToServiceListMapper(val mapper: ServiceViewToServiceMapper) :
    ListMapperImpl<ServiceView, Service>(mapper)