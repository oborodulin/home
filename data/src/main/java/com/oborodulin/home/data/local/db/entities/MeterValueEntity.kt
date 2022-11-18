package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.*

@Entity(
    tableName = MeterValueEntity.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = MeterEntity::class,
        parentColumns = arrayOf("meterId"),
        childColumns = arrayOf("metersId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class MeterValueEntity(
    @PrimaryKey var meterValueId: UUID = UUID.randomUUID(),
    val valueDate: Date = Date(),
    val meterValue: BigDecimal? = null,
    @ColumnInfo(index = true) var metersId: UUID,
) {
    companion object {
        const val TABLE_NAME = "meter_values"
    }
}