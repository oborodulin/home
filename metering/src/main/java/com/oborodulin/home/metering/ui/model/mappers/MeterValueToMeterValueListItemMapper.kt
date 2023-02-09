package com.oborodulin.home.metering.ui.model.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.metering.domain.model.MeterValue
import com.oborodulin.home.metering.ui.model.MeterValueListItem

class MeterValueToMeterValueListItemMapper : Mapper<MeterValue, MeterValueListItem> {
    override fun map(input: MeterValue) =
        MeterValueListItem(
            id = input.id,
            metersId = input.metersId,
            valueDate = input.valueDate,
            currentValue = input.meterValue,
        )
}