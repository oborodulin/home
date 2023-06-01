package com.oborodulin.home.billing.data.mappers

import com.oborodulin.home.billing.domain.model.PayerServiceSubtotal
import com.oborodulin.home.common.mapping.ListMapperImpl
import com.oborodulin.home.data.local.db.views.PayerServiceSubtotalDebtView

class PayerServiceSubtotalDebtViewListToPayerServiceDebtListMapper(mapper: PayerServiceSubtotalDebtViewToPayerServiceDebtMapper) :
    ListMapperImpl<PayerServiceSubtotalDebtView, PayerServiceSubtotal>(mapper)