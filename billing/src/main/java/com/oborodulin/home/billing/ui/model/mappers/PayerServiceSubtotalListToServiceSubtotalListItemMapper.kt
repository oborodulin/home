package com.oborodulin.home.billing.ui.model.mappers

import com.oborodulin.home.billing.domain.model.PayerServiceSubtotal
import com.oborodulin.home.billing.ui.model.ServiceSubtotalListItem
import com.oborodulin.home.common.mapping.ListMapperImpl

class PayerServiceSubtotalListToServiceSubtotalListItemMapper(mapper: PayerServiceSubtotalToServiceSubtotalListItemMapper) :
    ListMapperImpl<PayerServiceSubtotal, ServiceSubtotalListItem>(mapper)