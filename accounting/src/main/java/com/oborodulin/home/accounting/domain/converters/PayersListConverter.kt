package com.oborodulin.home.accounting.domain.converters

import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.accounting.data.mappers.PayerEntityMapper
import com.oborodulin.home.accounting.domain.model.Payer
import com.oborodulin.home.accounting.domain.usecases.GetPayersUseCase
import javax.inject.Inject

class PayersListConverter @Inject constructor(private val payerEntityMapper: PayerEntityMapper) :
    CommonResultConverter<GetPayersUseCase.Response, List<Payer>>() {

    override fun convertSuccess(data: GetPayersUseCase.Response): List<Payer> {
        return data.payers.map {
            payerEntityMapper.toPayer(it)
        }
    }
}