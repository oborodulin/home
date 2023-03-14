package com.oborodulin.home.metering.ui.model.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.views.MeterValuePrevPeriodView
import com.oborodulin.home.metering.ui.model.MeterValueListItem

//private const val TAG = "Metering.ui.PrevMetersValuesViewToMeterValueModelMapper"

class PrevMetersValuesViewToMeterValueListItemMapper :
    Mapper<MeterValuePrevPeriodView, MeterValueListItem> {
    override fun map(input: MeterValuePrevPeriodView): MeterValueListItem {
/*        val decimalSeparator = DecimalFormat().decimalFormatSymbols.decimalSeparator
        Timber.tag(TAG).d(
            "map(...) called: input.valueFormat = '%s'; decimalSeparator = '%s'",
            input.valueFormat,
            decimalSeparator
        )
        */
        return MeterValueListItem(
            id = input.meterValueId,
            serviceType = input.serviceType,
            serviceName = input.serviceName,
            payerId = input.payerId,
            metersId = input.meterId,
            meterType = input.meterType,
            meterMeasureUnit = input.measureUnit,
            prevLastDate = input.prevLastDate,
            prevValue = input.prevValue,
            currentValue = input.currentValue,
            valueFormat = input.valueFormat//.replace(',', decimalSeparator).replace('.', decimalSeparator)
        )
    }
}