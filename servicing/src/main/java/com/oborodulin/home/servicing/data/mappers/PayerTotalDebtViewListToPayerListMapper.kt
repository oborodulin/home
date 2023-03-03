package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.ListMapperImp
import com.oborodulin.home.data.local.db.views.PayerTotalDebtView
import com.oborodulin.home.domain.model.Payer

class PayerTotalDebtViewListToPayerListMapper(val mapper: PayerTotalDebtViewToPayerMapper) :
    ListMapperImp<PayerTotalDebtView, Payer>(mapper)