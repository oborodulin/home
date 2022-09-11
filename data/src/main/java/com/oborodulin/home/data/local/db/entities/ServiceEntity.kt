package com.oborodulin.home.data.local.db.entities

import androidx.room.Entity
import com.oborodulin.home.data.util.ServiceType

@Entity(tableName = ServiceEntity.TABLE_NAME)
class ServiceEntity(
    var pos: Int? = null,
    var type: ServiceType,
) : BaseEntity() {
    companion object {
        const val TABLE_NAME = "services"
    }
}
