package com.oborodulin.home.accounting.ui.model.converters

import com.oborodulin.home.accounting.ui.model.AccountingModel
import com.oborodulin.home.metering.ui.model.MeterValueModel
import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.metering.domain.usecases.GetPrevServiceMeterValuesUseCase

class PrevServiceMeterValuesConverter :
    CommonResultConverter<GetPrevServiceMeterValuesUseCase.Response, AccountingModel>() {

    override fun convertSuccess(data: GetPrevServiceMeterValuesUseCase.Response): AccountingModel {
        return AccountingModel(
            serviceMeterVals = data.prevServiceMeterValues.map {
                MeterValueModel(
                    id = it.meterValueId,
                    type = it.type,
                    name = it.name,
                    metersId = it.meterId,
                    measureUnit = it.measureUnit,
                    prevLastDate = it.prevLastDate,
                    prevValue = it.prevValue,
                )
            }
        )
    }
}