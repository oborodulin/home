package com.oborodulin.home.data.local.db.mappers

import com.oborodulin.home.common.mapping.ListMapperImpl
import com.oborodulin.home.data.local.db.entities.PayerEntity
import com.oborodulin.home.domain.model.Payer

class PayerEntityListToPayerListMapper(mapper: PayerEntityToPayerMapper) :
    ListMapperImpl<PayerEntity, Payer>(mapper)