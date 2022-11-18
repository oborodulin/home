package com.oborodulin.home.metering.ui.model.converters

import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.metering.domain.usecases.GetMetersUseCase
import com.oborodulin.home.metering.ui.model.MeteringModel

class MetersListConverter :
    CommonResultConverter<GetMetersUseCase.Response, List<MeteringModel>>() {

    override fun convertSuccess(data: GetMetersUseCase.Response): List<MeteringModel> {
        return listOf()
    }
}