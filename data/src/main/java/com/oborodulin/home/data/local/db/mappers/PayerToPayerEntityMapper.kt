package com.oborodulin.home.data.local.db.mappers

import com.oborodulin.home.common.mapping.Mapper
import com.oborodulin.home.data.local.db.entities.PayerEntity
import com.oborodulin.home.domain.model.Payer
import java.util.*

class PayerToPayerEntityMapper: Mapper<Payer, PayerEntity> {
    override fun map(input: Payer) = PayerEntity(
        payerId = input.id ?: input.apply { id = UUID.randomUUID() }.id!!,
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
}