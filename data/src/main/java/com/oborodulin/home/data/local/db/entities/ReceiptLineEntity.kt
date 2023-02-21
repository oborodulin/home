package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.math.BigDecimal
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
        entity = PayerServiceCrossRefEntity::class,
        parentColumns = arrayOf("payerServiceId"),
        childColumns = arrayOf("payersServicesId"),
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
        parentColumns = arrayOf("servicePromotionId"),
        childColumns = arrayOf("servicePromotionsId"),
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
    @PrimaryKey val receiptLineId: UUID = UUID.randomUUID(),
    val isServiceActive: Boolean = true,
    val isPromotionActive: Boolean = true,
    val isPrivileges: Boolean = false,
    val isAllocateRate: Boolean = false,
    val rateValue: BigDecimal,
    val additionalPayment: BigDecimal? = null,
    val isLinePaid: Boolean = false,
    @ColumnInfo(index = true) val receiptsId: UUID,
    @ColumnInfo(index = true) val payersServicesId: UUID,
    @ColumnInfo(index = true) val ratesId: UUID,
    @ColumnInfo(index = true) val servicePromotionsId: UUID? = null,
    @ColumnInfo(index = true) val meterValuesId: UUID? = null
) {
    companion object {
        const val TABLE_NAME = "receipt_lines"

        fun populateReceiptLinePaid(
            receiptId: UUID, payerServiceId: UUID, rateId: UUID, rateValue: BigDecimal,
            servicePromotionId: UUID? = null, meterValueId: UUID? = null
        ) = ReceiptLineEntity(
            receiptsId = receiptId,
            payersServicesId = payerServiceId,
            ratesId = rateId,
            rateValue = rateValue,
            servicePromotionsId = servicePromotionId,
            meterValuesId = meterValueId,
            isLinePaid = true
        )

        fun populateReceiptLineNotPaid(
            receiptId: UUID, payerServiceId: UUID, rateId: UUID, rateValue: BigDecimal,
            servicePromotionId: UUID? = null, meterValueId: UUID? = null
        ) = ReceiptLineEntity(
            receiptsId = receiptId,
            payersServicesId = payerServiceId,
            ratesId = rateId,
            rateValue = rateValue,
            servicePromotionsId = servicePromotionId,
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