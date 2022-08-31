package com.oborodulin.home.data.local.db.entities

import androidx.room.Entity
import androidx.room.Index
import com.oborodulin.home.domain.model.Service

@Entity(tableName = "services", indices = [Index(value = ["displayName"], unique = true)])
class ServiceEntity(
    var displayPos: Int,
    var displayName: String = "",
    var serviceDesc: String? = null,
    var isAllocateRate: Boolean = false,
) : BaseEntity()

fun ServiceEntity.toService(): Service {
    return Service(
        id = id,
        pos = displayPos,
        name = displayName,
        desc = serviceDesc,
        isAllocateRate = isAllocateRate
    )
}

