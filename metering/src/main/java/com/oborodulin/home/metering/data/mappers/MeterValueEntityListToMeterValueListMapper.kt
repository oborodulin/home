package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.common.mapping.ListMapperImp
import com.oborodulin.home.data.local.db.entities.MeterValueEntity
import com.oborodulin.home.metering.domain.model.MeterValue

class MeterValueEntityListToMeterValueListMapper(mapper: MeterValueEntityToMeterValueMapper) :
    ListMapperImp<MeterValueEntity, MeterValue>(mapper)