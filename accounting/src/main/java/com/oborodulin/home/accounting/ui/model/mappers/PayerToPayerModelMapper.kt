package com.oborodulin.home.accounting.ui.model.mappers

import com.oborodulin.home.accounting.ui.model.PayerModel
import com.oborodulin.home.common.ui.state.Mapper
import com.oborodulin.home.domain.model.Payer

class PayerToPayerModelMapper : Mapper<Payer, PayerModel> {
    override fun map(input: Payer) =
        PayerModel(
            id = input.id,
            ercCode = input.ercCode,
            fullName = input.fullName,
            address = input.address,
            totalArea = input.totalArea,
            livingSpace = input.livingSpace,
            heatedVolume = input.heatedVolume,
            paymentDay = input.paymentDay,
            personsNum = input.personsNum,
            isFavorite = input.isFavorite
        )
}