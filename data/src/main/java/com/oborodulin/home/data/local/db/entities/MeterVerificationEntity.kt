package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = MeterVerificationEntity.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = MeterEntity::class,
        parentColumns = arrayOf("meterId"),
        childColumns = arrayOf("metersId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class MeterVerificationEntity(
    @PrimaryKey var meterVerificationId: UUID = UUID.randomUUID(),
    val startDate: Date = Date(),
    val endDate: Date?,
    val isOk: Boolean = false,
    @ColumnInfo(index = true) var metersId: UUID,
) {
    companion object {
        const val TABLE_NAME = "meter_verifications"
    }
}