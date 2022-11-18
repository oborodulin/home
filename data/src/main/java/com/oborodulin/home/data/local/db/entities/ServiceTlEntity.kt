package com.oborodulin.home.data.local.db.entities

import androidx.room.*
import java.util.*

@Entity(
    tableName = ServiceTlEntity.TABLE_NAME,
    indices = [Index(value = ["localeCode", "servicesId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = ServiceEntity::class,
        parentColumns = arrayOf("serviceId"),
        childColumns = arrayOf("servicesId"),
        onDelete = ForeignKey.CASCADE
    )]
)
class ServiceTlEntity(
    @PrimaryKey var serviceTlId: UUID = UUID.randomUUID(),
    val localeCode: String = Locale.getDefault().language,
    val name: String = "",
    var measureUnit: String? = null,
    val descr: String? = null,
    @ColumnInfo(index = true) var servicesId: UUID,
) {
    companion object {
        const val TABLE_NAME = "services_tl"
    }
}


