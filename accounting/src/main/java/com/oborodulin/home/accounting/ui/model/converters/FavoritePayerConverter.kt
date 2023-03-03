package com.oborodulin.home.accounting.ui.model.converters

import com.oborodulin.home.accounting.domain.usecases.GetFavoritePayerUseCase
import com.oborodulin.home.accounting.ui.model.AccountingUi
import com.oborodulin.home.accounting.ui.model.mappers.PayerToPayerUiMapper
import com.oborodulin.home.common.ui.state.CommonResultConverter

class FavoritePayerConverter(
    private val mapper: PayerToPayerUiMapper
) : CommonResultConverter<GetFavoritePayerUseCase.Response, AccountingUi>() {
    override fun convertSuccess(data: GetFavoritePayerUseCase.Response) =
        AccountingUi(favoritePayer = mapper.map(data.payer))
}