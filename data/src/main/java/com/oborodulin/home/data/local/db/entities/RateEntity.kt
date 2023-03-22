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

        val DEF_ELECTRO_RANGE1: BigDecimal = BigDecimal.ZERO
        val DEF_ELECTRO_RANGE2: BigDecimal = BigDecimal("150")
        val DEF_ELECTRO_RANGE3: BigDecimal = BigDecimal("800")
        val DEF_ELECTRO_RANGE1_RATE: BigDecimal = BigDecimal("1.56")
        val DEF_ELECTRO_RANGE2_RATE: BigDecimal = BigDecimal("2.12")
        val DEF_ELECTRO_RANGE3_RATE: BigDecimal = BigDecimal("3.21")
        val DEF_ELECTRO_PRIV_RATE: BigDecimal = BigDecimal("0.92")
        val DEF_GAS_RATE: BigDecimal = BigDecimal("18.05")
        val DEF_COLD_WATER_RATE: BigDecimal = BigDecimal("25.02")
        val DEF_WASTE_RATE: BigDecimal = BigDecimal("11.61")
        val DEF_HEATING_RATE: BigDecimal = BigDecimal("1160.33")
        val DEF_HEATING_PAYER_RATE: BigDecimal = BigDecimal("14.76")
        val DEF_HOT_WATER_RATE: BigDecimal = BigDecimal("77.67")
        val DEF_RENT_PAYER_RATE: BigDecimal = BigDecimal("4.62")
        val DEF_GARBAGE_PAYER_RATE: BigDecimal = BigDecimal("15.73")
        val DEF_DOORPHONE_PAYER_RATE: BigDecimal = BigDecimal("35.00")
        val DEF_PHONE_PAYER_RATE: BigDecimal = BigDecimal("280")
        val DEF_INTERNET_PAYER_RATE: BigDecimal = BigDecimal("450")

        fun defaultRate(
            serviceId: UUID = UUID.randomUUID(), payerServiceId: UUID? = null,
            rateId: UUID = UUID.randomUUID(),
            startDate: OffsetDateTime = OffsetDateTime.now(),
            fromMeterValue: BigDecimal? = null,
            toMeterValue: BigDecimal? = null,
            rateValue: BigDecimal = BigDecimal.ZERO,
            isPerPerson: Boolean = false,
            isPrivileges: Boolean = false
        ) = RateEntity(
            servicesId = serviceId, payersServicesId = payerServiceId,
            rateId = rateId,
            startDate = startDate.minusMonths(5).withDayOfMonth(1),
            fromMeterValue = fromMeterValue,
            toMeterValue = toMeterValue,
            rateValue = rateValue,
            isPerPerson = isPerPerson,
            isPrivileges = isPrivileges
        )

        fun electricityRateFrom0To150(
            serviceId: UUID, payerServiceId: UUID? = null,
            startDate: OffsetDateTime = OffsetDateTime.now(),
            rateValue: BigDecimal = BigDecimal.ZERO
        ) = defaultRate(
            serviceId = serviceId, payerServiceId = payerServiceId, startDate = startDate,
            fromMeterValue = DEF_ELECTRO_RANGE1, toMeterValue = DEF_ELECTRO_RANGE2,
            rateValue = when (rateValue) {
                BigDecimal.ZERO -> DEF_ELECTRO_RANGE1_RATE
                else -> rateValue
            }
        )

        fun electricityRateFrom150To800(
            serviceId: UUID, payerServiceId: UUID? = null,
            startDate: OffsetDateTime = OffsetDateTime.now(),
            rateValue: BigDecimal = BigDecimal.ZERO
        ) = defaultRate(
            serviceId = serviceId, payerServiceId = payerServiceId, startDate = startDate,
            fromMeterValue = DEF_ELECTRO_RANGE2, toMeterValue = DEF_ELECTRO_RANGE3,
            rateValue = when (rateValue) {
                BigDecimal.ZERO -> DEF_ELECTRO_RANGE2_RATE
                else -> rateValue
            }
        )

        fun electricityRateFrom800(
            serviceId: UUID, payerServiceId: UUID? = null,
            startDate: OffsetDateTime = OffsetDateTime.now(),
            rateValue: BigDecimal = BigDecimal.ZERO
        ) = defaultRate(
            serviceId = serviceId, payerServiceId = payerServiceId, startDate = startDate,
            fromMeterValue = DEF_ELECTRO_RANGE3,
            rateValue = when (rateValue) {
                BigDecimal.ZERO -> DEF_ELECTRO_RANGE3_RATE
                else -> rateValue
            }
        )

        fun electricityPrivilegesRate(
            serviceId: UUID, payerServiceId: UUID? = null,
            startDate: OffsetDateTime = OffsetDateTime.now(),
            rateValue: BigDecimal = BigDecimal.ZERO
        ) = defaultRate(
            serviceId = serviceId, payerServiceId = payerServiceId, startDate = startDate,
            isPrivileges = true,
            rateValue = when (rateValue) {
                BigDecimal.ZERO -> DEF_ELECTRO_PRIV_RATE
                else -> rateValue
            }
        )

        fun gasRate(
            serviceId: UUID, startDate: OffsetDateTime = OffsetDateTime.now(),
            isPerPerson: Boolean = true, isPrivileges: Boolean = false,
            rateValue: BigDecimal = BigDecimal.ZERO
        ) = defaultRate(
            serviceId = serviceId, startDate = startDate,
            isPerPerson = isPerPerson, isPrivileges = isPrivileges,
            rateValue = when (rateValue) {
                BigDecimal.ZERO -> DEF_GAS_RATE
                else -> rateValue
            }
        )

        fun coldWaterRate(
            serviceId: UUID, startDate: OffsetDateTime = OffsetDateTime.now(),
            isPerPerson: Boolean = false, isPrivileges: Boolean = false,
            rateValue: BigDecimal = BigDecimal.ZERO
        ) = defaultRate(
            serviceId = serviceId, startDate = startDate,
            isPerPerson = isPerPerson, isPrivileges = isPrivileges,
            rateValue = when (rateValue) {
                BigDecimal.ZERO -> DEF_COLD_WATER_RATE
                else -> rateValue
            }
        )

        fun wasteRate(
            serviceId: UUID, startDate: OffsetDateTime = OffsetDateTime.now(),
            isPerPerson: Boolean = false, isPrivileges: Boolean = false,
            rateValue: BigDecimal = BigDecimal.ZERO
        ) = defaultRate(
            serviceId = serviceId, startDate = startDate,
            isPerPerson = isPerPerson, isPrivileges = isPrivileges,
            rateValue = when (rateValue) {
                BigDecimal.ZERO -> DEF_WASTE_RATE
                else -> rateValue
            }
        )

        fun heatingRate(
            serviceId: UUID, startDate: OffsetDateTime = OffsetDateTime.now(),
            isPerPerson: Boolean = false, isPrivileges: Boolean = false,
            rateValue: BigDecimal = BigDecimal.ZERO
        ) = defaultRate(
            serviceId = serviceId, startDate = startDate,
            isPerPerson = isPerPerson, isPrivileges = isPrivileges,
            rateValue = when (rateValue) {
                BigDecimal.ZERO -> DEF_HEATING_RATE
                else -> rateValue
            }
        )

        fun heatingRateForPayer(
            serviceId: UUID, payerServiceId: UUID, startDate: OffsetDateTime = OffsetDateTime.now(),
            isPerPerson: Boolean = false, isPrivileges: Boolean = false,
            rateValue: BigDecimal = BigDecimal.ZERO
        ) = defaultRate(
            serviceId = serviceId, payerServiceId = payerServiceId, startDate = startDate,
            isPerPerson = isPerPerson, isPrivileges = isPrivileges,
            rateValue = when (rateValue) {
                BigDecimal.ZERO -> DEF_HEATING_PAYER_RATE
                else -> rateValue
            }
        )

        fun hotWaterRate(
            serviceId: UUID, startDate: OffsetDateTime = OffsetDateTime.now(),
            isPerPerson: Boolean = false, isPrivileges: Boolean = false,
            rateValue: BigDecimal = BigDecimal.ZERO
        ) = defaultRate(
            serviceId = serviceId, startDate = startDate,
            isPerPerson = isPerPerson, isPrivileges = isPrivileges,
            rateValue = when (rateValue) {
                BigDecimal.ZERO -> DEF_HOT_WATER_RATE
                else -> rateValue
            }
        )

        fun rentRateForPayer(
            serviceId: UUID, payerServiceId: UUID, startDate: OffsetDateTime = OffsetDateTime.now(),
            isPerPerson: Boolean = false, isPrivileges: Boolean = false,
            rateValue: BigDecimal = BigDecimal.ZERO
        ) = defaultRate(
            serviceId = serviceId, payerServiceId = payerServiceId, startDate = startDate,
            isPerPerson = isPerPerson, isPrivileges = isPrivileges,
            rateValue = when (rateValue) {
                BigDecimal.ZERO -> DEF_RENT_PAYER_RATE
                else -> rateValue
            }
        )

        fun garbageRateForPayer(
            serviceId: UUID, payerServiceId: UUID, startDate: OffsetDateTime = OffsetDateTime.now(),
            isPerPerson: Boolean = true, isPrivileges: Boolean = false,
            rateValue: BigDecimal = BigDecimal.ZERO
        ) = defaultRate(
            serviceId = serviceId, payerServiceId = payerServiceId, startDate = startDate,
            isPerPerson = isPerPerson, isPrivileges = isPrivileges,
            rateValue = when (rateValue) {
                BigDecimal.ZERO -> DEF_GARBAGE_PAYER_RATE
                else -> rateValue
            }
        )

        fun doorphoneRateForPayer(
            serviceId: UUID, payerServiceId: UUID, startDate: OffsetDateTime = OffsetDateTime.now(),
            isPerPerson: Boolean = false, isPrivileges: Boolean = false,
            rateValue: BigDecimal = BigDecimal.ZERO
        ) = defaultRate(
            serviceId = serviceId, payerServiceId = payerServiceId, startDate = startDate,
            isPerPerson = isPerPerson, isPrivileges = isPrivileges,
            rateValue = when (rateValue) {
                BigDecimal.ZERO -> DEF_DOORPHONE_PAYER_RATE
                else -> rateValue
            }
        )

        fun phoneRateForPayer(
            serviceId: UUID, payerServiceId: UUID, startDate: OffsetDateTime = OffsetDateTime.now(),
            isPerPerson: Boolean = false, isPrivileges: Boolean = false,
            rateValue: BigDecimal = BigDecimal.ZERO
        ) = defaultRate(
            serviceId = serviceId, payerServiceId = payerServiceId, startDate = startDate,
            isPerPerson = isPerPerson, isPrivileges = isPrivileges,
            rateValue = when (rateValue) {
                BigDecimal.ZERO -> DEF_PHONE_PAYER_RATE
                else -> rateValue
            }
        )

        fun internetRateForPayer(
            serviceId: UUID, payerServiceId: UUID, startDate: OffsetDateTime = OffsetDateTime.now(),
            isPerPerson: Boolean = false, isPrivileges: Boolean = false,
            rateValue: BigDecimal = BigDecimal.ZERO
        ) = defaultRate(
            serviceId = serviceId, payerServiceId = payerServiceId, startDate = startDate,
            isPerPerson = isPerPerson, isPrivileges = isPrivileges,
            rateValue = when (rateValue) {
                BigDecimal.ZERO -> DEF_INTERNET_PAYER_RATE
                else -> rateValue
            }
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
