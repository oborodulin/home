package com.oborodulin.home.data.local.db.entities

import androidx.room.*
import com.oborodulin.home.data.util.Constants.DEF_PERSON_NUM
import java.math.BigDecimal
import java.time.OffsetDateTime
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
    val personsNum: Int = DEF_PERSON_NUM,
    val isReceiptPaid: Boolean = false,
    @ColumnInfo(index = true) val payersId: UUID,
) {
    companion object {
        const val TABLE_NAME = "receipts"

        fun defaultReceipt(
            payerId: UUID, receiptId: UUID = UUID.randomUUID(),
            receiptMonth: Int = OffsetDateTime.now().monthValue,
            receiptYear: Int = OffsetDateTime.now().year,
            totalArea: BigDecimal? = null,
            livingSpace: BigDecimal? = null,
            heatedVolume: BigDecimal? = null,
            personsNum: Int = DEF_PERSON_NUM,
            isReceiptPaid: Boolean = false
        ) = ReceiptEntity(
            receiptId = receiptId, payersId = payerId,
            receiptMonth = receiptMonth, receiptYear = receiptYear,
            totalArea = totalArea, livingSpace = livingSpace, heatedVolume = heatedVolume,
            personsNum = personsNum,
            isReceiptPaid = isReceiptPaid
        )

        fun receiptPaid(
            payerId: UUID,
            receiptMonth: Int = OffsetDateTime.now().monthValue,
            receiptYear: Int = OffsetDateTime.now().year
        ) = defaultReceipt(
            payerId = payerId,
            receiptMonth = receiptMonth,
            receiptYear = receiptYear,
            isReceiptPaid = true
        )

        fun receiptNotPaid(
            payerId: UUID,
            receiptMonth: Int = OffsetDateTime.now().monthValue,
            receiptYear: Int = OffsetDateTime.now().year
        ) =
            defaultReceipt(
                payerId = payerId,
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
