package com.oborodulin.home.data.local.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.oborodulin.home.common.data.entities.BaseEntity
import com.oborodulin.home.data.util.MeterType
import com.oborodulin.home.data.util.ServiceType
import java.util.UUID

@Entity(
    tableName = ServiceEntity.TABLE_NAME,
    indices = [Index(value = ["serviceType"], unique = true)]
)
data class ServiceEntity(
    @PrimaryKey val serviceId: UUID = UUID.randomUUID(),
    var servicePos: Int? = null,
    val serviceType: ServiceType,
    val serviceMeterType: MeterType = MeterType.NONE
) : BaseEntity() {

    companion object {
        const val TABLE_NAME = "services"

        fun defaultService(
            serviceId: UUID = UUID.randomUUID(),
            servicePos: Int? = null,
            serviceType: ServiceType = ServiceType.RENT,
            meterType: MeterType = MeterType.NONE
        ) = ServiceEntity(
            serviceId = serviceId,
            servicePos = servicePos,
            serviceType = serviceType,
            serviceMeterType = meterType
        )

        fun rent1Service(serviceId: UUID = UUID.randomUUID()) =
            defaultService(serviceId = serviceId, servicePos = 1, serviceType = ServiceType.RENT)

        fun electricity2Service(serviceId: UUID = UUID.randomUUID()) =
            defaultService(
                serviceId = serviceId,
                servicePos = 2,
                serviceType = ServiceType.ELECTRICITY,
                meterType = MeterType.ELECTRICITY
            )

        fun gas3Service(serviceId: UUID = UUID.randomUUID()) =
            defaultService(
                serviceId = serviceId,
                servicePos = 3,
                serviceType = ServiceType.GAS,
                meterType = MeterType.GAS
            )

        fun coldWater4Service(serviceId: UUID = UUID.randomUUID()) =
            defaultService(
                serviceId = serviceId,
                servicePos = 4,
                serviceType = ServiceType.COLD_WATER,
                meterType = MeterType.COLD_WATER
            )

        fun waste5Service(serviceId: UUID = UUID.randomUUID()) =
            defaultService(
                serviceId = serviceId,
                servicePos = 5,
                serviceType = ServiceType.WASTE,
                meterType = MeterType.HOT_WATER
            )

        fun heating6Service(serviceId: UUID = UUID.randomUUID()) =
            defaultService(
                serviceId = serviceId,
                servicePos = 6,
                serviceType = ServiceType.HEATING,
                meterType = MeterType.HEATING
            )

        fun hotWater7Service(serviceId: UUID = UUID.randomUUID()) =
            defaultService(
                serviceId = serviceId,
                servicePos = 7,
                serviceType = ServiceType.HOT_WATER,
                meterType = MeterType.HOT_WATER
            )

        fun garbage8Service(serviceId: UUID = UUID.randomUUID()) =
            defaultService(serviceId = serviceId, servicePos = 8, serviceType = ServiceType.GARBAGE)

        fun doorphone9Service(serviceId: UUID = UUID.randomUUID()) =
            defaultService(
                serviceId = serviceId,
                servicePos = 9,
                serviceType = ServiceType.DOORPHONE
            )

        fun phone10Service(serviceId: UUID = UUID.randomUUID()) =
            defaultService(serviceId = serviceId, servicePos = 10, serviceType = ServiceType.PHONE)

        fun ugso11Service(serviceId: UUID = UUID.randomUUID()) =
            defaultService(serviceId = serviceId, servicePos = 11, serviceType = ServiceType.USGO)

        fun internet12Service(serviceId: UUID = UUID.randomUUID()) =
            defaultService(
                serviceId = serviceId,
                servicePos = 12,
                serviceType = ServiceType.INTERNET
            )
    }

    override fun id() = this.serviceId

    override fun key() = serviceType.hashCode()

    override fun toString(): String {
        val str = StringBuffer()
        str.append("Service Entity '").append(serviceType).append("' №").append(servicePos)
        if (serviceMeterType != MeterType.NONE) {
            str.append(" for Meter '").append(serviceMeterType).append("'")
        }
        str.append(" serviceId = ").append(serviceId)
        return str.toString()
    }
}
