package com.oborodulin.home.data.local.db.entities

import androidx.room.*
import java.math.BigDecimal
import java.util.*

@Entity(
    tableName = ReceiptEntity.TABLE_NAME,
    indices = [Index(value = ["payersId", "receiptYear", "receiptMonth"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = PayerEntity::class,
        parentColumns = arrayOf("payerId"),
        childColumns = arrayOf("payersId"),
        onDelete = ForeignKey.CASCADE,
        deferred = true
    )]
)
class ReceiptEntity(
    @PrimaryKey val receiptId: UUID = UUID.randomUUID(),
    val receiptMonth: Int,
    val receiptYear: Int,
    val totalArea: BigDecimal? = null,
    val livingSpace: BigDecimal? = null,
    val heatedVolume: BigDecimal? = null,
    val personsNum: Int = 1,
    val isReceiptPaid: Boolean = false,
    @ColumnInfo(index = true) val payersId: UUID,
) {
    companion object {
        const val TABLE_NAME = "receipts"

        fun populateReceiptPaid(payerId: UUID, receiptMonth: Int, receiptYear: Int) = ReceiptEntity(
            payersId = payerId,
            receiptMonth = receiptMonth,
            receiptYear = receiptYear,
            isReceiptPaid = true
        )

        fun populateReceiptNotPaid(payerId: UUID, receiptMonth: Int, receiptYear: Int) =
            ReceiptEntity(
                payersId = payerId,
                receiptMonth = receiptMonth,
                receiptYear = receiptYear
            )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReceiptEntity
        if (receiptId != other.receiptId) return false

        return true
    }

    override fun hashCode(): Int {
        return receiptId.hashCode()
    }
}
