package com.oborodulin.home.metering.ui.model.mappers

import com.oborodulin.home.common.ui.state.Mapper
import com.oborodulin.home.data.local.db.views.PrevMetersValuesView
import com.oborodulin.home.metering.ui.model.MeterValueListItemModel

class PrevMetersValuesViewToMeterValueModelMapper : Mapper<PrevMetersValuesView, MeterValueListItemModel> {
    override fun map(input: PrevMetersValuesView) =
        MeterValueListItemModel(
            id = input.meterValueId,
            type = input.type,
            name = input.name,
            metersId = input.meterId,
            measureUnit = input.measureUnit,
            prevLastDate = input.prevLastDate,
            prevValue = input.prevValue,
            currentValue = input.currentValue,
            valueFormat = input.valueFormat
        )
}