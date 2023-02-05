package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.common.mapping.ListMapperImp
import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.metering.domain.model.Meter

class MeterListToMeterEntityListMapper(mapper: MeterToMeterEntityMapper) :
    ListMapperImp<Meter, MeterEntity>(mapper)