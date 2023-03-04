package com.oborodulin.home.data.local.db.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@Entity(
    tableName = RateEntity.TABLE_NAME,
    indices = [Index(
        value = ["startDate", "fromMeterValue", "isPerPerson", "isPrivileges", "servicesId", "payersServicesId"],
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

        fun populateElectricityRate1(serviceId: UUID) = RateEntity(
            servicesId = serviceId,
            fromMeterValue = BigDecimal.ZERO, toMeterValue = BigDecimal.valueOf(150),
            rateValue = BigDecimal.valueOf(1.56)
        )

        fun populateElectricityRate2(serviceId: UUID) = RateEntity(
            servicesId = serviceId, fromMeterValue = BigDecimal.valueOf(150),
            toMeterValue = BigDecimal.valueOf(800),
            rateValue = BigDecimal.valueOf(2.12)
        )

        fun populateElectricityRate3(serviceId: UUID) = RateEntity(
            servicesId = serviceId, fromMeterValue = BigDecimal.valueOf(800),
            rateValue = BigDecimal.valueOf(3.21)
        )

        fun populateElectricityPrivilegesRate(serviceId: UUID) = RateEntity(
            servicesId = serviceId, isPrivileges = true, rateValue = BigDecimal.valueOf(0.92)
        )

        fun populateGasRate(serviceId: UUID) = RateEntity(
            servicesId = serviceId, isPerPerson = true, rateValue = BigDecimal.valueOf(18.05)
        )

        fun populateColdWaterRate(serviceId: UUID) = RateEntity(
            servicesId = serviceId, rateValue = BigDecimal.valueOf(25.02)
        )

        fun populateWasteRate(serviceId: UUID) = RateEntity(
            servicesId = serviceId, rateValue = BigDecimal.valueOf(11.61)
        )

        fun populateHeatingRate(serviceId: UUID) = RateEntity(
            servicesId = serviceId, rateValue = BigDecimal.valueOf(1160.33)
        )

        fun populateHeatingRateForPayer(serviceId: UUID, payersServiceId: UUID) =
            RateEntity(
                servicesId = serviceId, payersServicesId = payersServiceId,
                rateValue = BigDecimal.valueOf(14.76)
            )

        fun populateHotWaterRate(serviceId: UUID) = RateEntity(
            servicesId = serviceId, rateValue = BigDecimal.valueOf(77.67)
        )

        fun populateRentRateForPayer(serviceId: UUID, payersServiceId: UUID) = RateEntity(
            servicesId = serviceId, payersServicesId = payersServiceId,
            rateValue = BigDecimal.valueOf(4.62)
        )

        fun populateGarbageRateForPayer(serviceId: UUID, payersServiceId: UUID) =
            RateEntity(
                servicesId = serviceId, payersServicesId = payersServiceId,
                isPerPerson = true, rateValue = BigDecimal.valueOf(15.73)
            )

        fun populateDoorphoneRateForPayer(serviceId: UUID, payersServiceId: UUID) =
            RateEntity(
                servicesId = serviceId, payersServicesId = payersServiceId,
                isPerPerson = true, rateValue = BigDecimal.valueOf(15.73)
            )

        fun populatePerPersonRate(
            serviceId: UUID, payersServiceId: UUID, rateId: UUID = UUID.randomUUID(),
            startDate: OffsetDateTime = OffsetDateTime.now(),
            fromMeterValue: BigDecimal? = null,
            toMeterValue: BigDecimal? = null,
            rateValue: BigDecimal,
            isPrivileges: Boolean = false
        ) = RateEntity(
            servicesId = serviceId, payersServicesId = payersServiceId,
            rateId = rateId,
            startDate = startDate,
            fromMeterValue = fromMeterValue,
            toMeterValue = toMeterValue,
            rateValue = rateValue,
            isPerPerson = true,
            isPrivileges = isPrivileges
        )

        fun populatePrivilegesRate(
            serviceId: UUID, payersServiceId: UUID, rateId: UUID = UUID.randomUUID(),
            startDate: OffsetDateTime = OffsetDateTime.now(),
            fromMeterValue: BigDecimal? = null,
            toMeterValue: BigDecimal? = null,
            rateValue: BigDecimal,
            isPerPerson: Boolean = false
        ) = RateEntity(
            servicesId = serviceId, payersServicesId = payersServiceId,
            rateId = rateId,
            startDate = startDate,
            fromMeterValue = fromMeterValue,
            toMeterValue = toMeterValue,
            rateValue = rateValue,
            isPerPerson = isPerPerson,
            isPrivileges = true
        )

        fun populateRate(
            serviceId: UUID, payersServiceId: UUID, rateId: UUID = UUID.randomUUID(),
            startDate: OffsetDateTime = OffsetDateTime.now(),
            fromMeterValue: BigDecimal? = null,
            toMeterValue: BigDecimal? = null,
            rateValue: BigDecimal,
            isPerPerson: Boolean = false,
            isPrivileges: Boolean = false
        ) = RateEntity(
            servicesId = serviceId, payersServicesId = payersServiceId,
            rateId = rateId,
            startDate = startDate,
            fromMeterValue = fromMeterValue,
            toMeterValue = toMeterValue,
            rateValue = rateValue,
            isPerPerson = isPerPerson,
            isPrivileges = isPrivileges
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
