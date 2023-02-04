package com.oborodulin.home.metering.ui.model.converters

import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.metering.domain.model.MeterValue
import com.oborodulin.home.metering.domain.usecases.SaveMeterValueUseCase
import com.oborodulin.home.metering.ui.model.MeterValueListItemModel

class MeterValueConverter(

) :
    CommonResultConverter<SaveMeterValueUseCase.Request, MeterValueListItemModel>() {

    override fun convertSuccess(data: SaveMeterValueUseCase.Request) =
        MeterValueListItemModel(
            id = data.meterValue.id,
            metersId = data.meterValue.metersId,
            valueDate = data.meterValue.valueDate,
            currentValue = data.meterValue.meterValue,
        )

    fun toMeterValue(meterValueListItemModel: MeterValueListItemModel): MeterValue {
        val meterValue = MeterValue(
            metersId = meterValueListItemModel.metersId,
            valueDate = meterValueListItemModel.valueDate,
            meterValue = meterValueListItemModel.currentValue,
        )
        meterValueListItemModel.id?.let { meterValue.id = it }
        return meterValue
    }
}