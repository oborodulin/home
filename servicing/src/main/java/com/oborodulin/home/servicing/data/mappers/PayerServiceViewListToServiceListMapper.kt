package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.ListMapperImp
import com.oborodulin.home.data.local.db.views.PayerServiceView
import com.oborodulin.home.servicing.domain.model.Service

class PayerServiceViewListToServiceListMapper(mapper: PayerServiceViewToServiceMapper) :
    ListMapperImp<PayerServiceView, Service>(mapper)
