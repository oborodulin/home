package com.oborodulin.home.billing.ui.model.mappers

import com.oborodulin.home.common.mapping.ListMapperImp
import com.oborodulin.home.servicing.domain.model.Service

class ServiceListToServiceSubtotalListItemMapper(mapper: ServiceToServiceSubtotalListItemMapper) :
    ListMapperImp<Service, com.oborodulin.home.billing.ui.model.ServiceSubtotalListItem>(mapper)