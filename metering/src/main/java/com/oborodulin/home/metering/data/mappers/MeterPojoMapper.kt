package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.data.local.db.entities.MeterTlEntity
import com.oborodulin.home.data.local.db.entities.pojo.MeterPojo
import com.oborodulin.home.domain.model.Service
import com.oborodulin.home.metering.domain.model.Meter
import com.oborodulin.home.metering.domain.model.MeterTl
import java.util.*

class MeterPojoMapper {
    fun toMeter(meterPojo: MeterPojo): Meter {
        val tl = MeterTl(measureUnit = meterPojo.measureUnit ?: "", descr = meterPojo.descr)
        tl.id = meterPojo.metersTlId
        val meter = Meter(
            num = meterPojo.num,
            maxValue = meterPojo.maxValue,
            passportDate = meterPojo.passportDate,
            verificationPeriod = meterPojo.verificationPeriod,
            tl = tl,
            service = Service(pos = 1),
        )
        meter.id = meterPojo.id
        return meter
    }

    fun toMeterEntity(meter: Meter): MeterEntity {
        val meterEntity = MeterEntity(
            num = meter.num,
            maxValue = meter.maxValue,
            passportDate = meter.passportDate,
            verificationPeriod = meter.verificationPeriod,
            payerServicesId = meter.service!!.id,
        )
        meterEntity.id = meter.id
        return meterEntity
    }

    fun toMeterTlEntity(meter: Meter): MeterTlEntity {
        val meterTlEntity = MeterTlEntity(
            metersId = meter.id,
            localeCode = Locale.getDefault().language,
            measureUnit = meter.tl.measureUnit,
            descr = meter.tl.descr,
        )
        meterTlEntity.id = meter.tl.id
        return meterTlEntity
    }
}