package com.oborodulin.home.servicing.ui.model.converters

import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.servicing.domain.usecases.GetServiceUseCase
import com.oborodulin.home.servicing.ui.model.ServiceUi
import com.oborodulin.home.servicing.ui.model.mappers.ServiceToServiceUiMapper

class ServiceConverter(
    private val mapper: ServiceToServiceUiMapper
) :
    CommonResultConverter<GetServiceUseCase.Response, ServiceUi>() {
    override fun convertSuccess(data: GetServiceUseCase.Response) = mapper.map(data.service)
}