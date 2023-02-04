package com.oborodulin.home.accounting.ui.model.converters

import com.oborodulin.home.accounting.ui.model.PayerListItemModel
import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.common.ui.state.ListMapper
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.domain.usecase.GetPayersUseCase

class PayersListConverter(
    private val mapper: ListMapper<Payer, PayerListItemModel>
) :
    CommonResultConverter<GetPayersUseCase.Response, List<PayerListItemModel>>() {
    override fun convertSuccess(data: GetPayersUseCase.Response) = mapper.map(data.payers)
}