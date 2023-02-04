package com.oborodulin.home.accounting.ui.model.converters

import com.oborodulin.home.accounting.ui.model.PayerModel
import com.oborodulin.home.accounting.ui.model.mappers.PayerToPayerModelMapper
import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.common.ui.state.Mapper
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.domain.usecase.GetPayerUseCase

class PayerConverter(
    private val mapper: PayerToPayerModelMapper
) :
    CommonResultConverter<GetPayerUseCase.Response, PayerModel>() {
    override fun convertSuccess(data: GetPayerUseCase.Response) = mapper.map(data.payer)
}