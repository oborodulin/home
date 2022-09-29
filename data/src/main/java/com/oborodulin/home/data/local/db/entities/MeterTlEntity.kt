package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.*

@Entity(
    tableName = MeterTlEntity.TABLE_NAME,
    indices = [Index(value = ["localeCode", "metersId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = MeterEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("metersId"),
        onDelete = ForeignKey.CASCADE
    )]
)
class MeterTlEntity(
    val localeCode: String,
    var measureUnit: String? = null,
    val descr: String? = null,
    @ColumnInfo(index = true) var metersId: UUID,
) : BaseEntity() {
    companion object {
        const val TABLE_NAME = "meters_tl"
    }
}


