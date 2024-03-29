package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.metering.domain.model.Meter
import java.util.*

class MeterToMeterEntityMapper : Mapper<Meter, MeterEntity> {
    override fun map(input: Meter) = MeterEntity(
        meterId = input.id ?: input.apply { id = UUID.randomUUID() }.id!!,
        meterType = input.meterType,
        meterNum = input.meterNum,
        maxValue = input.maxValue,
        passportDate = input.passportDate,
        initValue = input.initValue,
        verificationPeriod = input.verificationPeriod,
        payersId = input.payersId,
    )
}