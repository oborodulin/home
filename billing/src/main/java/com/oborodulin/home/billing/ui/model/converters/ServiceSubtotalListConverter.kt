package com.oborodulin.home.billing.ui.model.converters

import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.billing.domain.usecases.GetPayerServiceSubtotalsUseCase

class ServiceSubtotalListConverter(
    private val mapper: com.oborodulin.home.billing.ui.model.mappers.ServiceListToServiceSubtotalListItemMapper
) :
    CommonResultConverter<GetPayerServiceSubtotalsUseCase.Response, List<com.oborodulin.home.billing.ui.model.ServiceSubtotalListItem>>() {
    override fun convertSuccess(data: GetPayerServiceSubtotalsUseCase.Response) =
        mapper.map(data.services)
}