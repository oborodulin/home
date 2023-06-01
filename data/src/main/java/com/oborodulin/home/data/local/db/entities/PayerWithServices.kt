package com.oborodulin.home.data.local.db.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PayerWithServices(
    @Embedded
    val payer: PayerEntity,
    @Relation(
        parentColumn = "payerId",
        entityColumn = "serviceId",
        associateBy = Junction(
            PayerServiceCrossRefEntity::class,
            parentColumn = "payersId",
            entityColumn = "servicesId"
        )
    )
    val services: List<ServiceEntity>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PayerWithServices
        if (payer.id() != other.payer.id() || payer.key() != other.payer.key()) return false

        return true
    }

    override fun hashCode(): Int {
        return payer.hashCode()
    }
}
