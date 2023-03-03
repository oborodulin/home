package com.oborodulin.home.servicing.ui.model.converters

import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.servicing.domain.usecases.GetPayerServiceSubtotalsUseCase
import com.oborodulin.home.servicing.ui.model.ServiceSubtotalListItem
import com.oborodulin.home.servicing.ui.model.mappers.ServiceListToServiceSubtotalListItemMapper

class ServiceSubtotalListConverter(
    private val mapper: ServiceListToServiceSubtotalListItemMapper
) :
    CommonResultConverter<GetPayerServiceSubtotalsUseCase.Response, List<ServiceSubtotalListItem>>() {
    override fun convertSuccess(data: GetPayerServiceSubtotalsUseCase.Response) =
        mapper.map(data.services)
}