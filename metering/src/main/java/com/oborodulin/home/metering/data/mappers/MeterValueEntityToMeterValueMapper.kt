package com.oborodulin.home.metering.data.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.entities.MeterValueEntity
import com.oborodulin.home.metering.domain.model.MeterValue

class MeterValueEntityToMeterValueMapper : Mapper<MeterValueEntity, MeterValue> {
    override fun map(input: MeterValueEntity): MeterValue {
        val meterValue = MeterValue(
            valueDate = input.valueDate,
            meterValue = input.meterValue,
            metersId = input.metersId
        )
        meterValue.id = input.meterValueId
        return meterValue
    }
}