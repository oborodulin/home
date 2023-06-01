package com.oborodulin.home.servicing.ui.model.mappers

import com.oborodulin.home.common.mapping.ListMapperImpl
import com.oborodulin.home.servicing.domain.model.Service
import com.oborodulin.home.servicing.ui.model.ServiceListItem

class ServiceListToServiceListItemMapper(mapper: ServiceToServiceListItemMapper) :
    ListMapperImpl<Service, ServiceListItem>(mapper)