package com.oborodulin.home.metering.ui.model.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.views.MeterValuePrevPeriodsView
import com.oborodulin.home.metering.ui.model.MeterValueListItem
import timber.log.Timber
import java.text.DecimalFormat

//private const val TAG = "Metering.ui.PrevMetersValuesViewToMeterValueModelMapper"

class PrevMetersValuesViewToMeterValueModelMapper :
    Mapper<MeterValuePrevPeriodsView, MeterValueListItem> {
    override fun map(input: MeterValuePrevPeriodsView): MeterValueListItem {
/*        val decimalSeparator = DecimalFormat().decimalFormatSymbols.decimalSeparator
        Timber.tag(TAG).d(
            "map(...) called: input.valueFormat = '%s'; decimalSeparator = '%s'",
            input.valueFormat,
            decimalSeparator
        )
        */
        return MeterValueListItem(
            id = input.meterValueId,
            type = input.type,
            name = input.name,
            metersId = input.meterId,
            measureUnit = input.measureUnit,
            prevLastDate = input.prevLastDate,
            prevValue = input.prevValue,
            currentValue = input.currentValue,
            valueFormat = input.valueFormat//.replace(',', decimalSeparator).replace('.', decimalSeparator)
        )
    }
}