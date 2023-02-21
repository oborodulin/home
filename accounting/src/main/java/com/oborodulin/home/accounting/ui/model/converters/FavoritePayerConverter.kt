package com.oborodulin.home.accounting.ui.model.converters

import com.oborodulin.home.accounting.domain.usecases.GetFavoritePayerUseCase
import com.oborodulin.home.accounting.ui.model.AccountingModel
import com.oborodulin.home.accounting.ui.model.mappers.PayerToPayerModelMapper
import com.oborodulin.home.common.ui.state.CommonResultConverter

class FavoritePayerConverter(
    private val mapper: PayerToPayerModelMapper
) :
    CommonResultConverter<GetFavoritePayerUseCase.Response, AccountingModel>() {
    override fun convertSuccess(data: GetFavoritePayerUseCase.Response) =
        AccountingModel(mapper.map(data.payer))
}