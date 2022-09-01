package com.oborodulin.home.data.local.db.entities

import androidx.room.Entity
import androidx.room.Index
import com.oborodulin.home.domain.model.Service

@Entity(tableName = "services", indices = [Index(value = ["name"], unique = true)])
class ServiceEntity(
    var pos: Int,
    var name: String = "",
    var descr: String? = null,
) : BaseEntity()

fun ServiceEntity.toService(): Service {
    return Service(
        pos = pos,
        name = name,
        descr = descr,
    )
}

