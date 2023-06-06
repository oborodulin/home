package com.oborodulin.home.servicing.ui.model.converters

import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.servicing.domain.usecases.rate.GetRatesUseCase
import com.oborodulin.home.servicing.ui.model.RateListItem
import com.oborodulin.home.servicing.ui.model.mappers.RateListToRateListItemMapper

class RatesListConverter(
    private val mapper: RateListToRateListItemMapper
) :
    CommonResultConverter<GetRatesUseCase.Response, List<RateListItem>>() {
    override fun convertSuccess(data: GetRatesUseCase.Response) = mapper.map(data.rates)
}