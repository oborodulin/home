package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.data.local.db.entities.MeterValueEntity
import com.oborodulin.home.data.local.db.entities.MeterVerificationEntity
import com.oborodulin.home.metering.domain.model.Meter
import java.util.*

class MeterMapper {

    fun toMeterValueEntityList(meter: Meter) =
        meter.meterValues?.map {
            MeterValueEntity(
                meterValueId = it.id ?: it.apply { id = UUID.randomUUID() }.id!!,
                metersId = meter.id ?: meter.apply { id = UUID.randomUUID() }.id!!,
                valueDate = it.valueDate,
                meterValue = it.meterValue,
            )
        }

    fun toMeterVerificationEntityList(meter: Meter) =
        meter.meterVerifications?.map {
            MeterVerificationEntity(
                startDate = it.startDate,
                endDate = it.endDate,
                startMeterValue = it.startMeterValue,
                endMeterValue = it.endMeterValue,
                isOk = it.isOk,
                metersId = meter.id ?: meter.apply { id = UUID.randomUUID() }.id!!,
            )
        }
}