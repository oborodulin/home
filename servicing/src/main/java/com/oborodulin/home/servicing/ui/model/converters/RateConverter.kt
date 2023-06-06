package com.oborodulin.home.servicing.ui.model.converters

import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.servicing.domain.usecases.rate.GetRateUseCase
import com.oborodulin.home.servicing.ui.model.RateUi
import com.oborodulin.home.servicing.ui.model.mappers.RateToRateUiMapper

class RateConverter(
    private val mapper: RateToRateUiMapper
) :
    CommonResultConverter<GetRateUseCase.Response, RateUi>() {
    override fun convertSuccess(data: GetRateUseCase.Response) = mapper.map(data.rate)
}