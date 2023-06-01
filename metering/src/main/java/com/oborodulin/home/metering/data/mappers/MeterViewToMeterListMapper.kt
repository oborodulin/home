package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.common.mapping.ListMapperImpl
import com.oborodulin.home.data.local.db.views.MeterView
import com.oborodulin.home.metering.domain.model.Meter

class MeterViewToMeterListMapper(mapper: MeterViewToMeterMapper) :
    ListMapperImpl<MeterView, Meter>(mapper)