package com.oborodulin.home.billing.data.mappers

import com.oborodulin.home.billing.domain.model.PayerDebt
import com.oborodulin.home.common.mapping.ListMapperImpl
import com.oborodulin.home.data.local.db.views.PayerTotalDebtView

class PayerTotalDebtViewListToPayerDebtListMapper(val mapper: PayerTotalDebtViewToPayerDebtMapper) :
    ListMapperImpl<PayerTotalDebtView, PayerDebt>(mapper)