package com.oborodulin.home.metering.ui.model.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.metering.domain.model.MeterValue
import com.oborodulin.home.metering.ui.model.MeterValueListItemModel

class MeterValueToMeterValueListItemModelMapper : Mapper<MeterValue, MeterValueListItemModel> {
    override fun map(input: MeterValue) =
        MeterValueListItemModel(
            id = input.id,
            metersId = input.metersId,
            valueDate = input.valueDate,
            currentValue = input.meterValue,
        )
}