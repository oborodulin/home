package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.common.mapping.ListMapperImpl
import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.metering.domain.model.Meter

class MeterListToMeterEntityListMapper(mapper: MeterToMeterEntityMapper) :
    ListMapperImpl<Meter, MeterEntity>(mapper)