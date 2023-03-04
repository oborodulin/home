package com.oborodulin.home.billing.data.mappers

import com.oborodulin.home.billing.domain.model.Rate
import com.oborodulin.home.common.mapping.ListMapperImp
import com.oborodulin.home.data.local.db.entities.RateEntity

class RateEntityListToRateListMapper(mapper: RateEntityToRateMapper) :
    ListMapperImp<RateEntity, Rate>(mapper)