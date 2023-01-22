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
        onDelete = ForeignKey.CASCADE
        )]
    )
class ReceiptEntity(
    @PrimaryKey var receiptId: UUID = UUID.randomUUID(),
    var receiptMonth: Int,
    var receiptYear: Int,
    var isPaid: Boolean = false,
    @ColumnInfo(index = true) var payersId: UUID,
) {
    companion object {
        const val TABLE_NAME = "receipts"

        fun populateReceiptPaid(payerId: UUID, receiptMonth: Int, receiptYear: Int) = ReceiptEntity(
            payersId = payerId,
            receiptMonth = receiptMonth,
            receiptYear = receiptYear,
            isPaid = true
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
