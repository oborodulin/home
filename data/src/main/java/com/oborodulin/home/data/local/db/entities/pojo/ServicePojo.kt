package com.oborodulin.home.data.local.db.entities.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.oborodulin.home.data.local.db.entities.LanguageEntity
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.data.local.db.entities.ServiceTlEntity

data class ServicePojo(
    @Embedded
    val lang: LanguageEntity,
    @Relation(entity = ServiceTlEntity::class, parentColumn = "id", entityColumn = "LanguagesId")
    val context: ServiceContextPojo,
) {
    data class ServiceContextPojo(
        @Embedded
        val service: ServiceEntity,
        @Relation(parentColumn = "id", entityColumn = "servicesId")
        val tl: ServiceTlEntity,
    )
}
