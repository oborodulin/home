package com.oborodulin.home.accounting.ui.model.mappers

import com.oborodulin.home.accounting.ui.model.PayerListItemModel
import com.oborodulin.home.common.ui.state.Mapper
import com.oborodulin.home.domain.model.Payer
import java.util.*

class PayerToPayerListItemModelMapper : Mapper<Payer, PayerListItemModel> {
    override fun map(input: Payer) =
        PayerListItemModel(
            id = input.id ?: UUID.randomUUID(),
            fullName = input.fullName,
            address = input.address,
            totalArea = input.totalArea,
            livingSpace = input.livingSpace,
            paymentDay = input.paymentDay,
            personsNum = input.personsNum,
            isFavorite = input.isFavorite
        )
}