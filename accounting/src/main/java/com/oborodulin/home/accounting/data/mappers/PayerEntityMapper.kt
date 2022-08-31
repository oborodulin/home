package com.oborodulin.home.accounting.data.mappers

import com.oborodulin.home.data.local.db.entities.PayerEntity
import com.oborodulin.home.accounting.domain.model.Payer

class PayerEntityMapper {
    fun toPayer(payerEntity: PayerEntity): Payer {
        return Payer(
            id = payerEntity.id,
            ercCode = payerEntity.ercCode,
            fullName = payerEntity.fullName,
            address = payerEntity.address,
            totalArea = payerEntity.totalArea,
            livingSpace = payerEntity.livingSpace,
            heatedVolume = payerEntity.heatedVolume,
            paymentDay = payerEntity.paymentDay,
            personsNum = payerEntity.personsNum
        )
    }

    fun toPayerEntity(payer: Payer): PayerEntity {
        val payerEntity = PayerEntity(
            ercCode = payer.ercCode,
            fullName = payer.fullName,
            address = payer.address,
            totalArea = payer.totalArea,
            livingSpace = payer.livingSpace,
            heatedVolume = payer.heatedVolume,
            paymentDay = payer.paymentDay,
            personsNum = payer.personsNum
        )
        payerEntity.id = payer.id!!
        return payerEntity
    }
}