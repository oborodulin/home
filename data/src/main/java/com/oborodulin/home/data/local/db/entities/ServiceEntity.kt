package com.oborodulin.home.data.local.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.oborodulin.home.data.util.MeterType
import com.oborodulin.home.data.util.ServiceType
import java.util.*

@Entity(tableName = ServiceEntity.TABLE_NAME, indices = [Index(value = ["serviceType"], unique = true)])
data class ServiceEntity(
    @PrimaryKey val serviceId: UUID = UUID.randomUUID(),
    var servicePos: Int? = null,
    val serviceType: ServiceType,
    val meterType: MeterType = MeterType.NONE
) {
    companion object {
        const val TABLE_NAME = "services"

        fun populateRentService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(serviceId = serviceId, servicePos = 1, serviceType = ServiceType.RENT)

        fun populateElectricityService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(
                serviceId = serviceId,
                servicePos = 2,
                serviceType = ServiceType.ELECTRICITY,
                meterType = MeterType.ELECTRICITY
            )

        fun populateGasService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(
                serviceId = serviceId,
                servicePos = 3,
                serviceType = ServiceType.GAS,
                meterType = MeterType.GAS
            )

        fun populateColdWaterService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(
                serviceId = serviceId,
                servicePos = 4,
                serviceType = ServiceType.COLD_WATER,
                meterType = MeterType.COLD_WATER
            )

        fun populateWasteService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(
                serviceId = serviceId,
                servicePos = 5,
                serviceType = ServiceType.WASTE,
                meterType = MeterType.HOT_WATER
            )

        fun populateHeatingService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(
                serviceId = serviceId,
                servicePos = 6,
                serviceType = ServiceType.HEATING,
                meterType = MeterType.HEATING
            )

        fun populateHotWaterService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(
                serviceId = serviceId,
                servicePos = 7,
                serviceType = ServiceType.HOT_WATER,
                meterType = MeterType.HOT_WATER
            )

        fun populateGarbageService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(serviceId = serviceId, servicePos = 8, serviceType = ServiceType.GARBAGE)

        fun populateDoorphoneService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(
                serviceId = serviceId,
                servicePos = 9,
                serviceType = ServiceType.DOORPHONE
            )

        fun populatePhoneService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(serviceId = serviceId, servicePos = 10, serviceType = ServiceType.PHONE)

        fun populateUgsoService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(serviceId = serviceId, servicePos = 11, serviceType = ServiceType.USGO)

        fun populateInternetService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(
                serviceId = serviceId,
                servicePos = 12,
                serviceType = ServiceType.INTERNET
            )

        fun populateService(
            serviceId: UUID = UUID.randomUUID(),
            servicePos: Int? = null,
            serviceType: ServiceType,
            meterType: MeterType = MeterType.NONE
        ) = ServiceEntity(
            serviceId = serviceId,
            servicePos = servicePos,
            serviceType = serviceType,
            meterType = meterType
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServiceEntity
        if (serviceId != other.serviceId) return false

        return true
    }

    override fun hashCode(): Int {
        return serviceId.hashCode()
    }
}
