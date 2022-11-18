package com.oborodulin.home.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oborodulin.home.data.util.ServiceType
import java.util.*

@Entity(tableName = ServiceEntity.TABLE_NAME)
data class ServiceEntity(
    @PrimaryKey var serviceId: UUID = UUID.randomUUID(),
    var pos: Int? = null,
    var type: ServiceType,
) {
    companion object {
        const val TABLE_NAME = "services"
    }
}
