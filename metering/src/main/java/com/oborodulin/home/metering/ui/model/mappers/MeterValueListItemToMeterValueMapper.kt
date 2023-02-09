package com.oborodulin.home.metering.ui.model.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.metering.domain.model.MeterValue
import com.oborodulin.home.metering.ui.model.MeterValueListItem

class MeterValueListItemToMeterValueMapper : Mapper<MeterValueListItem, MeterValue> {
    override fun map(input: MeterValueListItem): MeterValue {
        val meterValue = MeterValue(
            metersId = input.metersId,
            valueDate = input.valueDate,
            meterValue = input.currentValue,
        )
        input.id?.let { meterValue.id = it }
        return meterValue
    }
}