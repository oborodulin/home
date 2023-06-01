package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.oborodulin.home.common.data.entities.BaseEntity
import com.oborodulin.home.data.util.Constants.DEF_PERSON_NUM
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

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
data class ReceiptEntity(
    @PrimaryKey val receiptId: UUID = UUID.randomUUID(),
    val receiptMonth: Int,
    val receiptYear: Int,
    val totalArea: BigDecimal? = null,
    val livingSpace: BigDecimal? = null,
    val heatedVolume: BigDecimal? = null,
    val personsNum: Int = DEF_PERSON_NUM,
    val isReceiptPaid: Boolean = false,
    @ColumnInfo(index = true) val payersId: UUID,
) : BaseEntity() {

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

    override fun id() = this.receiptId

    override fun key(): Int {
        var result = payersId.hashCode()
        result = result * 31 + receiptYear.hashCode()
        result = result * 31 + receiptMonth.hashCode()
        return result
    }
}
