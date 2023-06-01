package com.oborodulin.home.servicing.data.mappers

import com.oborodulin.home.common.mapping.ListMapperImpl
import com.oborodulin.home.data.local.db.entities.RateEntity

class RateEntityListToRateListMapper(mapper: RateEntityToRateMapper) :
    ListMapperImpl<RateEntity, com.oborodulin.home.servicing.domain.model.Rate>(mapper)