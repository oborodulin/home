package com.oborodulin.home.metering.ui.model.converters

import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.accounting.data.mappers.PayerEntityMapper
import com.oborodulin.home.accounting.domain.model.Payer
import com.oborodulin.home.accounting.domain.usecases.GetPayersUseCase
import com.oborodulin.home.metering.domain.usecases.GetMetersUseCase
import javax.inject.Inject

class MetersListConverter @Inject constructor(private val mapper: PayerEntityMapper) :
    CommonResultConverter<GetMetersUseCase.Response, List<Meters>>() {

    override fun convertSuccess(data: GetPayersUseCase.Response): List<Payer> {
        return data.payers.map {
            mapper.toPayer(it)
        }
    }
}