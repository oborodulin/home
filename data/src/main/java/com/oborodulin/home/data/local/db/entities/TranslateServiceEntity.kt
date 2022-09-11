package com.oborodulin.home.data.local.db.entities

import androidx.room.Embedded
import androidx.room.Relation

class TranslateServiceEntity(
    @Embedded
    val service: ServiceEntity,
    @Relation(parentColumn = "id", entityColumn = "servicesId", entity = ServiceTlEntity::class)
    val tl: ServiceTlEntity,
)

