package com.oborodulin.home.servicing.ui.model.converters

import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.servicing.domain.usecases.GetServicesUseCase
import com.oborodulin.home.servicing.ui.model.ServiceListItem
import com.oborodulin.home.servicing.ui.model.mappers.ServiceListToServiceListItemMapper

class ServicesListConverter(
    private val mapper: ServiceListToServiceListItemMapper
) :
    CommonResultConverter<GetServicesUseCase.Response, List<ServiceListItem>>() {
    override fun convertSuccess(data: GetServicesUseCase.Response) = mapper.map(data.services)
}