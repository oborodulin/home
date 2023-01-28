package com.oborodulin.home.data.local.db.mappers

import com.oborodulin.home.data.local.db.entities.PayerEntity
import com.oborodulin.home.domain.model.Payer
import java.util.*

class PayerEntityMapper {
    fun toPayer(payerEntity: PayerEntity): Payer {
        val payer = Payer(
            ercCode = payerEntity.ercCode,
            fullName = payerEntity.fullName,
            address = payerEntity.address,
            totalArea = payerEntity.totalArea,
            livingSpace = payerEntity.livingSpace,
            heatedVolume = payerEntity.heatedVolume,
            paymentDay = payerEntity.paymentDay,
            personsNum = payerEntity.personsNum,
            isFavorite = payerEntity.isFavorite,
        )
        payer.id = payerEntity.payerId
        return payer
    }

    fun toPayerEntity(payer: Payer) = PayerEntity(
        payerId = payer.id ?: payer.apply { id = UUID.randomUUID() }.id!!,
        ercCode = payer.ercCode,
        fullName = payer.fullName,
        address = payer.address,
        totalArea = payer.totalArea,
        livingSpace = payer.livingSpace,
        heatedVolume = payer.heatedVolume,
        paymentDay = payer.paymentDay,
        personsNum = payer.personsNum,
        isFavorite = payer.isFavorite,
    )
}