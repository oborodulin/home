package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@Entity(
    tableName = MeterVerificationEntity.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = MeterEntity::class,
        parentColumns = arrayOf("meterId"),
        childColumns = arrayOf("metersId"),
        onDelete = ForeignKey.CASCADE,
        deferred = true
    )]
)
data class MeterVerificationEntity(
    @PrimaryKey var meterVerificationId: UUID = UUID.randomUUID(),
    val startDate: OffsetDateTime = OffsetDateTime.now(),
    val endDate: OffsetDateTime?,
    val startMeterValue: BigDecimal,
    val endMeterValue: BigDecimal? = null,
    val isOk: Boolean = false,
    @ColumnInfo(index = true) var metersId: UUID,
) : BaseEntity() {

    override fun id() = this.meterVerificationId

    companion object {
        const val TABLE_NAME = "meter_verifications"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MeterVerificationEntity
        if (meterVerificationId != other.meterVerificationId) return false

        return true
    }
}