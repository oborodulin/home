package com.oborodulin.home.accounting.ui.model.converters

import com.oborodulin.home.accounting.ui.model.PayerUi
import com.oborodulin.home.accounting.ui.model.mappers.PayerToPayerUiMapper
import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.domain.usecases.GetPayerUseCase

class PayerConverter(
    private val mapper: PayerToPayerUiMapper
) :
    CommonResultConverter<GetPayerUseCase.Response, PayerUi>() {
    override fun convertSuccess(data: GetPayerUseCase.Response) = mapper.map(data.payer)
}