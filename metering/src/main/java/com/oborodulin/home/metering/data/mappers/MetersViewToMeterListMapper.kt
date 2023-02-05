package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.common.mapping.ListMapperImp
import com.oborodulin.home.data.local.db.views.MetersView
import com.oborodulin.home.metering.domain.model.Meter

class MetersViewToMeterListMapper(mapper: MetersViewToMeterMapper) :
    ListMapperImp<MetersView, Meter>(mapper)