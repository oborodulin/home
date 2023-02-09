package com.oborodulin.home.accounting.ui.model.mappers

import com.oborodulin.home.accounting.ui.model.PayerListItem
import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.domain.model.Payer
import java.util.*

class PayerToPayerListItemModelMapper : Mapper<Payer, PayerListItem> {
    override fun map(input: Payer) =
        PayerListItem(
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