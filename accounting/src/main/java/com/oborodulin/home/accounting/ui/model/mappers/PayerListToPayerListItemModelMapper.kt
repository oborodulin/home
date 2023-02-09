package com.oborodulin.home.accounting.ui.model.mappers

import com.oborodulin.home.accounting.ui.model.PayerListItem
import com.oborodulin.home.common.mapping.ListMapperImp
import com.oborodulin.home.domain.model.Payer

class PayerListToPayerListItemModelMapper(mapper: PayerToPayerListItemModelMapper) :
    ListMapperImp<Payer, PayerListItem>(mapper)