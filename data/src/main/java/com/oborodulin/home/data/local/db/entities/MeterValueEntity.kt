package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.math.BigDecimal
import java.util.*

@Entity(
    tableName = MeterValueEntity.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = MeterEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("metersId"),
        onDelete = ForeignKey.CASCADE
    )]
)
class MeterValueEntity(
    val valueDate: Date = Date(),
    val meterValue: BigDecimal? = null,
    @ColumnInfo(index = true) var metersId: UUID,
) : BaseEntity() {
    companion object {
        const val TABLE_NAME = "meter_values"
    }
}