package com.oborodulin.home.data.local.db.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@Entity(
    tableName = RateEntity.TABLE_NAME,
    indices = [Index(
        value = ["servicesId", "payersServicesId", "startDate", "fromMeterValue", "isPerPerson", "isPrivileges"],
        unique = true
    )],
    foreignKeys = [ForeignKey(
        entity = ServiceEntity::class,
        parentColumns = arrayOf("serviceId"),
        childColumns = arrayOf("servicesId"),
        onDelete = CASCADE
    ), ForeignKey(
        entity = PayerServiceCrossRefEntity::class,
        parentColumns = arrayOf("payerServiceId"),
        childColumns = arrayOf("payersServicesId"),
        onDelete = CASCADE
    )]
)
class RateEntity(
    @PrimaryKey val rateId: UUID = UUID.randomUUID(),
    val startDate: OffsetDateTime = OffsetDateTime.now(),
    val fromMeterValue: BigDecimal? = null,
    val toMeterValue: BigDecimal? = null,
    val rateValue: BigDecimal,
    val isPerPerson: Boolean = false, // считаем по норме на 1 человека, но приоритет для тарифов по счётчику
    val isPrivileges: Boolean = false, // считаем по счётчику, но по льготному тарифу
    @ColumnInfo(index = true) val servicesId: UUID,
    @ColumnInfo(index = true) val payersServicesId: UUID? = null
) {
    companion object {
        const val TABLE_NAME = "rates"

        fun defaultRate(
            serviceId: UUID, payerServiceId: UUID? = null, rateId: UUID = UUID.randomUUID(),
            startDate: OffsetDateTime = OffsetDateTime.now(),
            fromMeterValue: BigDecimal? = null,
            toMeterValue: BigDecimal? = null,
            rateValue: BigDecimal,
            isPerPerson: Boolean = false,
            isPrivileges: Boolean = false
        ) = RateEntity(
            servicesId = serviceId, payersServicesId = payerServiceId,
            rateId = rateId,
            startDate = startDate,
            fromMeterValue = fromMeterValue,
            toMeterValue = toMeterValue,
            rateValue = rateValue,
            isPerPerson = isPerPerson,
            isPrivileges = isPrivileges
        )

        fun electricityRateFrom0To150(serviceId: UUID) = defaultRate(
            serviceId = serviceId,
            fromMeterValue = BigDecimal.ZERO, toMeterValue = BigDecimal.valueOf(150),
            rateValue = BigDecimal.valueOf(1.56)
        )

        fun electricityRateFrom150To800(serviceId: UUID) = defaultRate(
            serviceId = serviceId, fromMeterValue = BigDecimal.valueOf(150),
            toMeterValue = BigDecimal.valueOf(800),
            rateValue = BigDecimal.valueOf(2.12)
        )

        fun electricityRateFrom800(serviceId: UUID) = defaultRate(
            serviceId = serviceId, fromMeterValue = BigDecimal.valueOf(800),
            rateValue = BigDecimal.valueOf(3.21)
        )

        fun electricityPrivilegesRate(serviceId: UUID) = defaultRate(
            serviceId = serviceId, isPrivileges = true, rateValue = BigDecimal.valueOf(0.92)
        )

        fun gasRate(serviceId: UUID) = defaultRate(
            serviceId = serviceId, isPerPerson = true, rateValue = BigDecimal.valueOf(18.05)
        )

        fun coldWaterRate(serviceId: UUID) = defaultRate(
            serviceId = serviceId, rateValue = BigDecimal.valueOf(25.02)
        )

        fun wasteRate(serviceId: UUID) = defaultRate(
            serviceId = serviceId, rateValue = BigDecimal.valueOf(11.61)
        )

        fun heatingRate(serviceId: UUID) = defaultRate(
            serviceId = serviceId, rateValue = BigDecimal.valueOf(1160.33)
        )

        fun heatingRateForPayer(serviceId: UUID, payerServiceId: UUID) =
            defaultRate(
                serviceId = serviceId, payerServiceId = payerServiceId,
                rateValue = BigDecimal.valueOf(14.76)
            )

        fun hotWaterRate(serviceId: UUID) = defaultRate(
            serviceId = serviceId, rateValue = BigDecimal.valueOf(77.67)
        )

        fun rentRateForPayer(serviceId: UUID, payerServiceId: UUID) = defaultRate(
            serviceId = serviceId, payerServiceId = payerServiceId,
            rateValue = BigDecimal.valueOf(4.62)
        )

        fun garbageRateForPayer(serviceId: UUID, payerServiceId: UUID) =
            defaultRate(
                serviceId = serviceId, payerServiceId = payerServiceId,
                isPerPerson = true, rateValue = BigDecimal.valueOf(15.73)
            )

        fun doorphoneRateForPayer(serviceId: UUID, payerServiceId: UUID) =
            defaultRate(
                serviceId = serviceId, payerServiceId = payerServiceId,
                isPerPerson = true, rateValue = BigDecimal.valueOf(15.73)
            )

        fun perPersonRate(
            serviceId: UUID, payerServiceId: UUID, rateId: UUID = UUID.randomUUID(),
            startDate: OffsetDateTime = OffsetDateTime.now(),
            fromMeterValue: BigDecimal? = null,
            toMeterValue: BigDecimal? = null,
            rateValue: BigDecimal,
            isPrivileges: Boolean = false
        ) = defaultRate(
            serviceId = serviceId, payerServiceId = payerServiceId,
            rateId = rateId,
            startDate = startDate,
            fromMeterValue = fromMeterValue,
            toMeterValue = toMeterValue,
            rateValue = rateValue,
            isPerPerson = true,
            isPrivileges = isPrivileges
        )

        fun privilegesRate(
            serviceId: UUID, payerServiceId: UUID, rateId: UUID = UUID.randomUUID(),
            startDate: OffsetDateTime = OffsetDateTime.now(),
            fromMeterValue: BigDecimal? = null,
            toMeterValue: BigDecimal? = null,
            rateValue: BigDecimal,
            isPerPerson: Boolean = false
        ) = defaultRate(
            serviceId = serviceId, payerServiceId = payerServiceId,
            rateId = rateId,
            startDate = startDate,
            fromMeterValue = fromMeterValue,
            toMeterValue = toMeterValue,
            rateValue = rateValue,
            isPerPerson = isPerPerson,
            isPrivileges = true
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RateEntity
        if (rateId != other.rateId) return false

        return true
    }

    override fun hashCode(): Int {
        return rateId.hashCode()
    }
}
