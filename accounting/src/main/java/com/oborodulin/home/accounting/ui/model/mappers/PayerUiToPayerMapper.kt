package com.oborodulin.home.accounting.ui.model.mappers

import com.oborodulin.home.accounting.ui.model.PayerUi
import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.domain.model.Payer

class PayerUiToPayerMapper : Mapper<PayerUi, Payer> {
    override fun map(input: PayerUi): Payer {
        val payer = Payer(
            ercCode = input.ercCode,
            fullName = input.fullName,
            address = input.address,
            totalArea = input.totalArea,
            livingSpace = input.livingSpace,
            heatedVolume = input.heatedVolume,
            paymentDay = input.paymentDay,
            personsNum = input.personsNum,
            isAlignByPaymentDay = input.isAlignByPaymentDay,
            isFavorite = input.isFavorite
        )
        payer.id = input.id
        return payer
    }
}