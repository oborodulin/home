package com.oborodulin.home.accounting.ui.model.converters

import com.oborodulin.home.accounting.ui.model.AccountingModel
import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.common.ui.state.Mapper
import com.oborodulin.home.data.local.db.views.PrevMetersValuesView
import com.oborodulin.home.metering.domain.usecases.GetPrevServiceMeterValuesUseCase
import com.oborodulin.home.metering.ui.model.MeterValueModel

class PrevServiceMeterValuesConverter(
    private val mapper: Mapper<PrevMetersValuesView, MeterValueModel>
) :
    CommonResultConverter<GetPrevServiceMeterValuesUseCase.Response, AccountingModel>() {

    override fun convertSuccess(data: GetPrevServiceMeterValuesUseCase.Response): AccountingModel {
        return AccountingModel(
            serviceMeterVals = data.prevServiceMeterValues.map { mapper.map(it) }
        )
    }
}