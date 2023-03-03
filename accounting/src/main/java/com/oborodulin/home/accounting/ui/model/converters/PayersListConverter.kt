package com.oborodulin.home.accounting.ui.model.converters

import com.oborodulin.home.accounting.ui.model.PayerListItem
import com.oborodulin.home.accounting.ui.model.mappers.PayerListToPayerListItemMapper
import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.accounting.domain.usecases.GetPayersUseCase

class PayersListConverter(
    private val mapper: PayerListToPayerListItemMapper
) :
    CommonResultConverter<GetPayersUseCase.Response, List<PayerListItem>>() {
    override fun convertSuccess(data: GetPayersUseCase.Response) = mapper.map(data.payers)
}