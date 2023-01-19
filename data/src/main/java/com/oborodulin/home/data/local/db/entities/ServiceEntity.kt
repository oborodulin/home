package com.oborodulin.home.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oborodulin.home.data.util.ServiceType
import java.util.*

@Entity(tableName = ServiceEntity.TABLE_NAME)
data class ServiceEntity(
    @PrimaryKey var serviceId: UUID = UUID.randomUUID(),
    var pos: Int? = null,
    var type: ServiceType,
) {
    companion object {
        const val TABLE_NAME = "services"

        fun populateRentService() = ServiceEntity(pos = 1, type = ServiceType.RENT)
        fun populateElectricityService() = ServiceEntity(pos = 2, type = ServiceType.ELECRICITY)
        fun populateGasService() = ServiceEntity(pos = 3, type = ServiceType.GAS)
        fun populateColdWaterService() = ServiceEntity(pos = 4, type = ServiceType.COLD_WATER)
        fun populateWasteService() = ServiceEntity(pos = 5, type = ServiceType.WASTE)
        fun populateHeatingService() = ServiceEntity(pos = 6, type = ServiceType.HEATING)
        fun populateHotWaterService() = ServiceEntity(pos = 7, type = ServiceType.HOT_WATER)
        fun populateGarbageService() = ServiceEntity(pos = 8, type = ServiceType.GARBAGE)
        fun populateDoorphoneService() = ServiceEntity(pos = 9, type = ServiceType.DOORPHONE)
        fun populatePhoneService() = ServiceEntity(pos = 10, type = ServiceType.PHONE)
        fun populateUgsoService() = ServiceEntity(pos = 11, type = ServiceType.USGO)
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
