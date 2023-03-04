package com.oborodulin.home.data.local.db.entities

import android.content.Context
import androidx.room.*
import com.oborodulin.home.data.R
import java.util.*

@Entity(
    tableName = ServiceTlEntity.TABLE_NAME,
    indices = [Index(value = ["localeCode", "servicesId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = ServiceEntity::class,
        parentColumns = arrayOf("serviceId"),
        childColumns = arrayOf("servicesId"),
        onDelete = ForeignKey.CASCADE,
        deferred = true
    )]
)
class ServiceTlEntity(
    @PrimaryKey val serviceTlId: UUID = UUID.randomUUID(),
    val localeCode: String = Locale.getDefault().language,
    val serviceName: String = "",
    var measureUnit: String? = null,
    val serviceDesc: String? = null,
    @ColumnInfo(index = true) var servicesId: UUID,
) {
    companion object {
        const val TABLE_NAME = "services_tl"

        fun populateRentServiceTl(ctx: Context, serviceId: UUID) =
            ServiceTlEntity(
                serviceName = ctx.resources.getString(R.string.service_rent),
                servicesId = serviceId
            )

        fun populateElectricityServiceTl(ctx: Context, serviceId: UUID) =
            ServiceTlEntity(
                serviceName = ctx.resources.getString(R.string.service_electricity),
                measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.kWh_unit),
                servicesId = serviceId
            )

        fun populateGasServiceTl(ctx: Context, serviceId: UUID) =
            ServiceTlEntity(
                serviceName = ctx.resources.getString(R.string.service_gas),
                measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
                servicesId = serviceId
            )

        fun populateColdWaterServiceTl(ctx: Context, serviceId: UUID) =
            ServiceTlEntity(
                serviceName = ctx.resources.getString(R.string.service_cold_water),
                measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
                servicesId = serviceId
            )

        fun populateWasteServiceTl(ctx: Context, serviceId: UUID) =
            ServiceTlEntity(
                serviceName = ctx.resources.getString(R.string.service_waste),
                measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
                servicesId = serviceId
            )

        fun populateHeatingServiceTl(ctx: Context, serviceId: UUID) =
            ServiceTlEntity(
                serviceName = ctx.resources.getString(R.string.service_heating),
                measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.Gcal_unit),
                servicesId = serviceId
            )

        fun populateHotWaterServiceTl(ctx: Context, serviceId: UUID) =
            ServiceTlEntity(
                serviceName = ctx.resources.getString(R.string.service_hot_water),
                measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
                servicesId = serviceId
            )

        fun populateGarbageServiceTl(ctx: Context, serviceId: UUID) =
            ServiceTlEntity(
                serviceName = ctx.resources.getString(R.string.service_garbage),
                servicesId = serviceId
            )

        fun populateDoorphoneServiceTl(ctx: Context, serviceId: UUID) =
            ServiceTlEntity(
                serviceName = ctx.resources.getString(R.string.service_doorphone),
                servicesId = serviceId
            )

        fun populatePhoneServiceTl(ctx: Context, serviceId: UUID) =
            ServiceTlEntity(
                serviceName = ctx.resources.getString(R.string.service_phone),
                servicesId = serviceId
            )

        fun populateUgsoServiceTl(ctx: Context, serviceId: UUID) =
            ServiceTlEntity(
                serviceName = ctx.resources.getString(R.string.service_ugso),
                servicesId = serviceId
            )

        fun populateInternetServiceTl(ctx: Context, serviceId: UUID) =
            ServiceTlEntity(
                serviceName = ctx.resources.getString(R.string.service_internet),
                servicesId = serviceId
            )

        fun populateServiceTl(
            serviceId: UUID, serviceTlId: UUID = UUID.randomUUID(),
            localeCode: String = Locale.getDefault().language,
            serviceName: String = "",
            measureUnit: String? = null,
            serviceDesc: String? = null
        ) = ServiceTlEntity(
            servicesId = serviceId,
            serviceTlId = serviceTlId,
            localeCode = localeCode,
            serviceName = serviceName,
            measureUnit = measureUnit,
            serviceDesc = serviceDesc
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServiceTlEntity
        if (serviceTlId != other.serviceTlId) return false

        return true
    }

    override fun hashCode(): Int {
        return serviceTlId.hashCode()
    }
}


