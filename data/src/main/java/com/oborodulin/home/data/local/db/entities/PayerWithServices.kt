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
)
