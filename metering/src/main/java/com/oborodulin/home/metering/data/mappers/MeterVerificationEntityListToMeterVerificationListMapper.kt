package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.common.mapping.ListMapperImp
import com.oborodulin.home.data.local.db.entities.MeterVerificationEntity
import com.oborodulin.home.metering.domain.model.MeterVerification

class MeterVerificationEntityListToMeterVerificationListMapper(mapper: MeterVerificationEntityToMeterVerificationMapper) :
    ListMapperImp<MeterVerificationEntity, MeterVerification>(mapper)