package com.oborodulin.home.data.local.db.mappers

import com.oborodulin.home.common.ui.state.Mapper
import com.oborodulin.home.data.local.db.entities.PayerEntity
import com.oborodulin.home.domain.model.Payer

class PayerEntityToPayerMapper: Mapper<PayerEntity, Payer> {
    override fun map(input: PayerEntity): Payer {
        val payer = Payer(
            ercCode = input.ercCode,
            fullName = input.fullName,
            address = input.address,
            totalArea = input.totalArea,
            livingSpace = input.livingSpace,
            heatedVolume = input.heatedVolume,
            paymentDay = input.paymentDay,
            personsNum = input.personsNum,
            isFavorite = input.isFavorite,
        )
        payer.id = input.payerId
        return payer
    }
}