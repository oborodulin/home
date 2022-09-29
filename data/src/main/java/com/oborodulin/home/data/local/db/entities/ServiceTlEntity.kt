package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.*

@Entity(
    tableName = ServiceTlEntity.TABLE_NAME,
    indices = [Index(value = ["localeCode", "servicesId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = ServiceEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("servicesId"),
        onDelete = ForeignKey.CASCADE
    )]
)
class ServiceTlEntity(
    val localeCode: String = Locale.getDefault().language,
    val name: String = "",
    var measureUnit: String? = null,
    val descr: String? = null,
    @ColumnInfo(index = true) var servicesId: UUID,
) : BaseEntity() {
    companion object {
        const val TABLE_NAME = "services_tl"
    }
}


