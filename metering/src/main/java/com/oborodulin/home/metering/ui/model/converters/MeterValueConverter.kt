package com.oborodulin.home.metering.ui.model.converters

import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.metering.domain.model.MeterValue
import com.oborodulin.home.metering.domain.usecases.SaveMeterValueUseCase
import com.oborodulin.home.metering.ui.model.MeterValueModel

class MeterValueConverter :
    CommonResultConverter<SaveMeterValueUseCase.Request, MeterValueModel>() {

    override fun convertSuccess(data: SaveMeterValueUseCase.Request) =
        MeterValueModel(
            id = data.meterValue.id,
            metersId = data.meterValue.metersId,
            valueDate = data.meterValue.valueDate,
            currentValue = data.meterValue.meterValue,
        )

    fun toMeterValue(meterValueModel: MeterValueModel): MeterValue {
        val meterValue = MeterValue(
            metersId = meterValueModel.metersId,
            valueDate = meterValueModel.valueDate,
            meterValue = meterValueModel.currentValue,
        )
        meterValue.id = meterValueModel.id
        return meterValue
    }
}