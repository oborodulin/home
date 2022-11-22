package com.oborodulin.home.accounting.ui.model.converters

import com.oborodulin.home.accounting.ui.model.PayerListItemModel
import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.domain.usecase.GetPayersUseCase

class PayersListConverter :
    CommonResultConverter<GetPayersUseCase.Response, List<PayerListItemModel>>() {

    override fun convertSuccess(data: GetPayersUseCase.Response) =
        data.payers.map {
            PayerListItemModel(
                id = it.id,
                fullName = it.fullName,
                address = it.address,
                totalArea = it.totalArea,
                livingSpace = it.livingSpace,
                paymentDay = it.paymentDay,
                personsNum = it.personsNum,
                isFavorite = it.isFavorite
            )
        }

}