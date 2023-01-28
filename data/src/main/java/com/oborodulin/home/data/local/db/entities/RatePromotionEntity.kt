package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = RatePromotionEntity.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = RateEntity::class,
        parentColumns = arrayOf("rateId"),
        childColumns = arrayOf("ratesId"),
        onDelete = ForeignKey.CASCADE,
        deferred = true
    )]
)
class RatePromotionEntity(
    @PrimaryKey var ratePromotionId: UUID = UUID.randomUUID(),
    val paymentMonths: Int = 1, // период оплаты - кол-во месяцев в году
    val isPrevRate: Boolean = false, // считаем по предыдущему тарифу
    val isActive: Boolean = true,
    @ColumnInfo(index = true) var ratesId: UUID,
) {
    companion object {
        const val TABLE_NAME = "rate_promotions"

        fun populatePrevRatePromotion(rateId: UUID) = RatePromotionEntity(
            ratesId = rateId,
            paymentMonths = 10,
            isPrevRate = true
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RatePromotionEntity
        if (ratePromotionId != other.ratePromotionId) return false

        return true
    }

    override fun hashCode(): Int {
        return ratePromotionId.hashCode()
    }
}