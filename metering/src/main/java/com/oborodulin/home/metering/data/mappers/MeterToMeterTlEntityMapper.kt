package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.entities.MeterTlEntity
import com.oborodulin.home.metering.domain.model.Meter
import java.util.*

class MeterToMeterTlEntityMapper : Mapper<Meter, MeterTlEntity> {
    override fun map(input: Meter) = MeterTlEntity(
        meterTlId = input.tl.id ?: input.tl.apply { id = UUID.randomUUID() }.id!!,
        metersId = input.id ?: input.apply { id = UUID.randomUUID() }.id!!,
        localeCode = Locale.getDefault().language,
        measureUnit = input.tl.measureUnit,
        descr = input.tl.descr,
    )
}