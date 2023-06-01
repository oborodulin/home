package com.oborodulin.home.accounting.ui.model.mappers

import com.oborodulin.home.accounting.ui.model.PayerListItem
import com.oborodulin.home.common.mapping.ListMapperImpl
import com.oborodulin.home.domain.model.Payer

class PayerListToPayerListItemMapper(mapper: PayerToPayerListItemMapper) :
    ListMapperImpl<Payer, PayerListItem>(mapper)