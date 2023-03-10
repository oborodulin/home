package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.views.MeterView
import com.oborodulin.home.metering.domain.model.Meter
import com.oborodulin.home.metering.domain.model.MeterTl

class MeterViewToMeterMapper : Mapper<MeterView, Meter> {
    override fun map(input: MeterView): Meter {
        val tl = MeterTl(measureUnit = input.tl.meterMeasureUnit, meterDesc = input.tl.meterDesc)
        tl.id = input.tl.meterTlId
        val meter = Meter(
            payersId = input.data.payersId,
            meterType = input.data.meterType,
            meterNum = input.data.meterNum,
            maxValue = input.data.maxValue,
            passportDate = input.data.passportDate,
            verificationPeriod = input.data.verificationPeriod,
            tl = tl
        )
        meter.id = input.data.meterId
        return meter
    }
}