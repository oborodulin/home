package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.entities.MeterVerificationEntity
import com.oborodulin.home.metering.domain.model.MeterVerification

class MeterVerificationEntityToMeterVerificationMapper :
    Mapper<MeterVerificationEntity, MeterVerification> {
    override fun map(input: MeterVerificationEntity): MeterVerification {
        val meterVerification = MeterVerification(
            startDate = input.startDate,
            endDate = input.endDate,
            startMeterValue = input.startMeterValue,
            endMeterValue = input.endMeterValue,
            isOk = input.isOk,
        )
        meterVerification.id = input.meterVerificationId
        return meterVerification
    }
}