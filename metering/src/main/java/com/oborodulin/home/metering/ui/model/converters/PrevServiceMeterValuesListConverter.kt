package com.oborodulin.home.metering.ui.model.converters

import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.metering.domain.usecases.GetPrevServiceMeterValuesUseCase
import com.oborodulin.home.metering.ui.model.MeterValueListItemModel
import com.oborodulin.home.metering.ui.model.mappers.PrevMetersValuesViewToMeterValueModelMapper

class PrevServiceMeterValuesListConverter(
    private val mapper: PrevMetersValuesViewToMeterValueModelMapper
) :
    CommonResultConverter<GetPrevServiceMeterValuesUseCase.Response, List<MeterValueListItemModel>>() {
    override fun convertSuccess(data: GetPrevServiceMeterValuesUseCase.Response) =
        data.prevServiceMeterValues.map { mapper.map(it) }
}
