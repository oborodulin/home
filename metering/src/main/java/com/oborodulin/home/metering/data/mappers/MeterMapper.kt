package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.data.local.db.entities.MeterEntity
import com.oborodulin.home.data.local.db.entities.MeterTlEntity
import com.oborodulin.home.data.local.db.entities.MeterValueEntity
import com.oborodulin.home.data.local.db.entities.MeterVerificationEntity
import com.oborodulin.home.data.local.db.views.MetersView
import com.oborodulin.home.metering.domain.model.Meter
import com.oborodulin.home.metering.domain.model.MeterTl
import com.oborodulin.home.metering.domain.model.MeterValue
import com.oborodulin.home.metering.domain.model.MeterVerification
import java.util.*

class MeterMapper {
    fun toMeter(metersView: MetersView): Meter {
        val tl = MeterTl(measureUnit = metersView.tl.measureUnit ?: "", descr = metersView.tl.descr)
        tl.id = metersView.tl.meterTlId
        val meter = Meter(
            payersServicesId = metersView.ps.payerServiceId,
            num = metersView.data.num,
            maxValue = metersView.data.maxValue,
            passportDate = metersView.data.passportDate,
            verificationPeriod = metersView.data.verificationPeriod,
            tl = tl
        )
        meter.id = metersView.data.meterId
        return meter
    }

    fun toMeterValue(meterValueEntity: MeterValueEntity): MeterValue {
        val meterValue = MeterValue(
            valueDate = meterValueEntity.valueDate,
            meterValue = meterValueEntity.meterValue,
            metersId = meterValueEntity.metersId
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

    fun toMeterValueEntity(meterValue: MeterValue) =
        MeterValueEntity(
            meterValueId = meterValue.id,
            valueDate = meterValue.valueDate,
            meterValue = meterValue.meterValue,
            metersId = meterValue.metersId
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