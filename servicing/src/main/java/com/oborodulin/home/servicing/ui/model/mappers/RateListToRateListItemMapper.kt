package com.oborodulin.home.servicing.ui.model.mappers

import com.oborodulin.home.common.mapping.ListMapperImpl
import com.oborodulin.home.servicing.domain.model.Rate
import com.oborodulin.home.servicing.ui.model.RateListItem

class RateListToRateListItemMapper(mapper: RateToRateListItemMapper) :
    ListMapperImpl<Rate, RateListItem>(mapper)