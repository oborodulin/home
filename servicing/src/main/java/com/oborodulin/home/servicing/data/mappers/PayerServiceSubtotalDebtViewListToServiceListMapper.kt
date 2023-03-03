package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.ListMapperImp
import com.oborodulin.home.data.local.db.views.PayerServiceSubtotalDebtView
import com.oborodulin.home.servicing.domain.model.Service

class PayerServiceSubtotalDebtViewListToServiceListMapper(mapper: PayerServiceSubtotalDebtViewToServiceMapper) :
    ListMapperImp<PayerServiceSubtotalDebtView, Service>(mapper)