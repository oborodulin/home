package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.common.mapping.ListMapperImp
import com.oborodulin.home.data.local.db.views.MeterView
import com.oborodulin.home.metering.domain.model.Meter

class MeterViewToMeterListMapper(mapper: MeterViewToMeterMapper) :
    ListMapperImp<MeterView, Meter>(mapper)