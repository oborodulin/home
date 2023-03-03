package com.oborodulin.home.servicing.ui.model.mappers

import com.oborodulin.home.common.mapping.ListMapperImp
import com.oborodulin.home.servicing.domain.model.Service
import com.oborodulin.home.servicing.ui.model.ServiceSubtotalListItem

class ServiceListToServiceSubtotalListItemMapper(mapper: ServiceToServiceSubtotalListItemMapper) :
    ListMapperImp<Service, ServiceSubtotalListItem>(mapper)