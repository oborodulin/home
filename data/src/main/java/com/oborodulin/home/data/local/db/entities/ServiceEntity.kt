package com.oborodulin.home.data.local.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.oborodulin.home.data.util.MeterType
import com.oborodulin.home.data.util.ServiceType
import java.util.*

@Entity(tableName = ServiceEntity.TABLE_NAME, indices = [Index(value = ["type"], unique = true)])
data class ServiceEntity(
    @PrimaryKey val serviceId: UUID = UUID.randomUUID(),
    var pos: Int? = null,
    val type: ServiceType,
    val meterType: MeterType = MeterType.NONE
) {
    companion object {
        const val TABLE_NAME = "services"

        fun populateRentService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(serviceId = serviceId, pos = 1, type = ServiceType.RENT)

        fun populateElectricityService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(
                serviceId = serviceId,
                pos = 2,
                type = ServiceType.ELECTRICITY,
                meterType = MeterType.ELECTRICITY
            )

        fun populateGasService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(
                serviceId = serviceId,
                pos = 3,
                type = ServiceType.GAS,
                meterType = MeterType.GAS
            )

        fun populateColdWaterService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(
                serviceId = serviceId,
                pos = 4,
                type = ServiceType.COLD_WATER,
                meterType = MeterType.COLD_WATER
            )

        fun populateWasteService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(
                serviceId = serviceId,
                pos = 5,
                type = ServiceType.WASTE,
                meterType = MeterType.HOT_WATER
            )

        fun populateHeatingService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(
                serviceId = serviceId,
                pos = 6,
                type = ServiceType.HEATING,
                meterType = MeterType.HEATING
            )

        fun populateHotWaterService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(
                serviceId = serviceId,
                pos = 7,
                type = ServiceType.HOT_WATER,
                meterType = MeterType.HOT_WATER
            )

        fun populateGarbageService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(serviceId = serviceId, pos = 8, type = ServiceType.GARBAGE)

        fun populateDoorphoneService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(serviceId = serviceId, pos = 9, type = ServiceType.DOORPHONE)

        fun populatePhoneService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(serviceId = serviceId, pos = 10, type = ServiceType.PHONE)

        fun populateUgsoService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(serviceId = serviceId, pos = 11, type = ServiceType.USGO)

        fun populateInternetService(serviceId: UUID = UUID.randomUUID()) =
            ServiceEntity(serviceId = serviceId, pos = 12, type = ServiceType.INTERNET)
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
