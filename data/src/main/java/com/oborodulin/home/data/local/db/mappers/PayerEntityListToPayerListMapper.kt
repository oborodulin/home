package com.oborodulin.home.data.local.db.mappers

import com.oborodulin.home.common.ui.state.ListMapperImp
import com.oborodulin.home.data.local.db.entities.PayerEntity
import com.oborodulin.home.domain.model.Payer

class PayerEntityListToPayerListMapper(mapper: PayerEntityToPayerMapper) :
    ListMapperImp<PayerEntity, Payer>(mapper)