package com.oborodulin.home.accounting.ui.model.mappers

import com.oborodulin.home.accounting.ui.model.PayerUi
import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.domain.model.Payer

class PayerToPayerUiMapper : Mapper<Payer, PayerUi> {
    override fun map(input: Payer) =
        PayerUi(
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