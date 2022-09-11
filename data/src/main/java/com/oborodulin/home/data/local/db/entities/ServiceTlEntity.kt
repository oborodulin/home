package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.*

@Entity(
    tableName = ServiceTlEntity.TABLE_NAME,
    indices = [Index(value = ["languagesId", "servicesId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = ServiceEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("servicesId"),
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = LanguageEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("languagesId"),
            onDelete = ForeignKey.CASCADE
        )]
)
class ServiceTlEntity(
    val name: String = "",
    var measureUnit: String? = null,
    val descr: String? = null,
    @ColumnInfo(index = true) var servicesId: UUID,
    @ColumnInfo(index = true) var languagesId: UUID,
) : BaseEntity() {
    companion object {
        const val TABLE_NAME = "services_tl"
    }
}


