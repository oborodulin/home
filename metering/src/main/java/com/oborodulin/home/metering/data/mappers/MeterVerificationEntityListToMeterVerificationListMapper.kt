package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.common.mapping.ListMapperImpl
import com.oborodulin.home.data.local.db.entities.MeterVerificationEntity
import com.oborodulin.home.metering.domain.model.MeterVerification

class MeterVerificationEntityListToMeterVerificationListMapper(mapper: MeterVerificationEntityToMeterVerificationMapper) :
    ListMapperImpl<MeterVerificationEntity, MeterVerification>(mapper)