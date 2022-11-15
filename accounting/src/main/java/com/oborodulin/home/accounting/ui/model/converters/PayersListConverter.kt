package com.oborodulin.home.accounting.ui.model.converters

import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.data.local.db.mappers.PayerEntityMapper
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.domain.usecase.GetPayersUseCase
import javax.inject.Inject

class PayersListConverter @Inject constructor(private val mapper: PayerEntityMapper) :
    CommonResultConverter<GetPayersUseCase.Response, List<Payer>>() {

    override fun convertSuccess(data: GetPayersUseCase.Response): List<Payer> {
        return data.payers.map {
            mapper.toPayer(it)
        }
    }
}