package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.entities.MeterValueEntity
import com.oborodulin.home.metering.domain.model.MeterValue
import java.util.*

class MeterValueToMeterValueEntityMapper : Mapper<MeterValue, MeterValueEntity> {
    override fun map(input: MeterValue) = MeterValueEntity(
        meterValueId = input.id ?: input.apply { id = UUID.randomUUID() }.id!!,
        valueDate = input.valueDate,
        meterValue = input.meterValue,
        metersId = input.metersId
    )
}