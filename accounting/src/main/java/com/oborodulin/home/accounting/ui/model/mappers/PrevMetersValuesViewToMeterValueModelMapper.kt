package com.oborodulin.home.accounting.ui.model.mappers

import com.oborodulin.home.common.ui.state.Mapper
import com.oborodulin.home.data.local.db.views.PrevMetersValuesView
import com.oborodulin.home.metering.ui.model.MeterValueModel

class PrevMetersValuesViewToMeterValueModelMapper : Mapper<PrevMetersValuesView, MeterValueModel> {
    override fun map(input: PrevMetersValuesView) =
        MeterValueModel(
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