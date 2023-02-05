package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.views.MetersView
import com.oborodulin.home.metering.domain.model.Meter
import com.oborodulin.home.metering.domain.model.MeterTl

class MetersViewToMeterMapper : Mapper<MetersView, Meter> {
    override fun map(input: MetersView): Meter {
        val tl = MeterTl(measureUnit = input.tl.measureUnit, descr = input.tl.descr)
        tl.id = input.tl.meterTlId
        val meter = Meter(
            payersServicesId = input.ps.payerServiceId,
            num = input.data.num,
            maxValue = input.data.maxValue,
            passportDate = input.data.passportDate,
            verificationPeriod = input.data.verificationPeriod,
            tl = tl
        )
        meter.id = input.data.meterId
        return meter
    }
}