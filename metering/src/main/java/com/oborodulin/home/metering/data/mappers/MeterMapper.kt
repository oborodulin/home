package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.data.local.db.entities.MeterTlEntity
import com.oborodulin.home.data.local.db.entities.MeterValueEntity
import com.oborodulin.home.data.local.db.entities.MeterVerificationEntity
import com.oborodulin.home.data.local.db.views.MeterView
import com.oborodulin.home.metering.domain.model.Meter
import com.oborodulin.home.metering.domain.model.MeterTl
import com.oborodulin.home.metering.domain.model.MeterValue
import com.oborodulin.home.metering.domain.model.MeterVerification
import java.util.*

class MeterMapper {
    fun toMeter(meterView: MeterView): Meter {
        val tl = MeterTl(measureUnit = meterView.tl.measureUnit ?: "", descr = meterView.tl.descr)
        tl.id = meterView.tl.meterTlId
        val meter = Meter(
            payersServicesId = meterView.ps.payerServiceId,
            num = meterView.data.num,
            maxValue = meterView.data.maxValue,
            passportDate = meterView.data.passportDate,
            verificationPeriod = meterView.data.verificationPeriod,
            tl = tl
        )
        meter.id = meterView.data.meterId
        return meter
    }

    fun toMeterValue(meterValueEntity: MeterValueEntity): MeterValue {
        val meterValue = MeterValue(
            valueDate = meterValueEntity.valueDate,
            meterValue = meterValueEntity.meterValue,
        )
        meterValue.id = meterValueEntity.meterValueId
        return meterValue
    }

    fun toMeterVerification(meterVerificationEntity: MeterVerificationEntity): MeterVerification {
        val meterVerification = MeterVerification(
            startDate = meterVerificationEntity.startDate,
            endDate = meterVerificationEntity.endDate,
            isOk = meterVerificationEntity.isOk,
        )
        meterVerification.id = meterVerificationEntity.meterVerificationId
        return meterVerification
    }

    fun toMeterEntity(meter: Meter) =
        MeterEntity(
            meterId = meter.id,
            num = meter.num,
            maxValue = meter.maxValue,
            passportDate = meter.passportDate,
            verificationPeriod = meter.verificationPeriod,
            payersServicesId = meter.payersServicesId,
        )

    fun toMeterTlEntity(meter: Meter) =
        MeterTlEntity(
            meterTlId = meter.tl.id,
            metersId = meter.id,
            localeCode = Locale.getDefault().language,
            measureUnit = meter.tl.measureUnit,
            descr = meter.tl.descr,
        )

    fun toMeterValueEntityList(meter: Meter) =
        meter.meterValues?.map {
            MeterValueEntity(
                meterValueId = it.id,
                valueDate = it.valueDate,
                meterValue = it.meterValue,
                metersId = meter.id
            )
        }

    fun toMeterVerificationEntityList(meter: Meter) =
        meter.meterVerifications?.map {
            MeterVerificationEntity(
                startDate = it.startDate,
                endDate = it.endDate,
                isOk = it.isOk,
                metersId = meter.id
            )
        }
}