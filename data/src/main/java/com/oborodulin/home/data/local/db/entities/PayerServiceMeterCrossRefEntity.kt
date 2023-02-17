package com.oborodulin.home.data.local.db.entities

import androidx.room.*
import java.util.*

@Entity(
    tableName = PayerServiceMeterCrossRefEntity.TABLE_NAME,
    indices = [Index(value = ["payersServicesId", "metersId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = PayerServiceCrossRefEntity::class,
        parentColumns = arrayOf("payerServiceId"),
        childColumns = arrayOf("payersServicesId"),
        onDelete = ForeignKey.CASCADE,
        deferred = true
    ), ForeignKey(
        entity = MeterEntity::class,
        parentColumns = arrayOf("meterId"),
        childColumns = arrayOf("metersId"),
        onDelete = ForeignKey.CASCADE,
        deferred = true
    )]
)
data class PayerServiceMeterCrossRefEntity(
    @PrimaryKey val payerServiceMeterId: UUID = UUID.randomUUID(),
    @ColumnInfo(index = true) val payersServicesId: UUID,
    @ColumnInfo(index = true) val metersId: UUID,
) {
    companion object {
        const val TABLE_NAME = "payers_services_meters"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PayerServiceMeterCrossRefEntity
        if (payerServiceMeterId != other.payerServiceMeterId) return false

        return true
    }

    override fun hashCode(): Int {
        return payerServiceMeterId.hashCode()
    }
}