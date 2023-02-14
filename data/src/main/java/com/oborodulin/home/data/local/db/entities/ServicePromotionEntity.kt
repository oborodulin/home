package com.oborodulin.home.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = ServicePromotionEntity.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = ServiceEntity::class,
        parentColumns = arrayOf("serviceId"),
        childColumns = arrayOf("servicesId"),
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = PayerServiceCrossRefEntity::class,
        parentColumns = arrayOf("payerServiceId"),
        childColumns = arrayOf("payersServicesId"),
        onDelete = ForeignKey.CASCADE
    )]
)
class ServicePromotionEntity(
    @PrimaryKey var servicePromotionId: UUID = UUID.randomUUID(),
    val paymentMonths: Int = 1, // период оплаты - кол-во месяцев в году
    val isPrevRate: Boolean = false, // считаем по предыдущему тарифу
    val isActive: Boolean = true,
    @ColumnInfo(index = true) var servicesId: UUID,
    @ColumnInfo(index = true) var payersServicesId: UUID? = null,
) {
    companion object {
        const val TABLE_NAME = "service_promotions"

        fun populatePrevRatePromotion(serviceId: UUID, payerServiceId: UUID? = null) =
            ServicePromotionEntity(
                servicesId = serviceId,
                payersServicesId = payerServiceId,
                paymentMonths = 10,
                isPrevRate = true
            )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServicePromotionEntity
        if (servicePromotionId != other.servicePromotionId) return false

        return true
    }

    override fun hashCode(): Int {
        return servicePromotionId.hashCode()
    }
}