package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = ReceiptLineEntity.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = ReceiptEntity::class,
        parentColumns = arrayOf("receiptId"),
        childColumns = arrayOf("receiptsId"),
        onDelete = ForeignKey.CASCADE,
        deferred = true
    ), ForeignKey(
        entity = RateEntity::class,
        parentColumns = arrayOf("rateId"),
        childColumns = arrayOf("ratesId"),
        onDelete = ForeignKey.CASCADE,
        deferred = true
    ), ForeignKey(
        entity = ServicePromotionEntity::class,
        parentColumns = arrayOf("ratePromotionId"),
        childColumns = arrayOf("ratePromotionsId"),
        onDelete = ForeignKey.CASCADE,
        deferred = true
    ), ForeignKey(
        entity = MeterValueEntity::class,
        parentColumns = arrayOf("meterValueId"),
        childColumns = arrayOf("meterValuesId"),
        onDelete = ForeignKey.CASCADE,
        deferred = true
    )]
)
class ReceiptLineEntity(
    @PrimaryKey var receiptLineId: UUID = UUID.randomUUID(),
    val isPaid: Boolean = false,
    @ColumnInfo(index = true) val receiptsId: UUID,
    @ColumnInfo(index = true) val ratesId: UUID,
    @ColumnInfo(index = true) val ratePromotionsId: UUID? = null,
    @ColumnInfo(index = true) val meterValuesId: UUID? = null
) {
    companion object {
        const val TABLE_NAME = "receipt_lines"

        fun populateReceiptLine(
            receiptId: UUID, rateId: UUID, ratePromotionId: UUID? = null, meterValueId: UUID? = null
        ) = ReceiptLineEntity(
            receiptsId = receiptId,
            ratesId = rateId,
            ratePromotionsId = ratePromotionId,
            meterValuesId = meterValueId,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReceiptLineEntity
        if (receiptLineId != other.receiptLineId) return false

        return true
    }

    override fun hashCode(): Int {
        return receiptLineId.hashCode()
    }
}