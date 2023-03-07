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
    @ColumnInfo(index = true) val servicesId: UUID,
) {
    companion object {
        const val TABLE_NAME = "services_tl"

        fun defaultServiceTl(
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

        fun rentServiceTl(ctx: Context, serviceId: UUID) =
            defaultServiceTl(
                serviceName = ctx.resources.getString(R.string.service_rent),
                serviceId = serviceId
            )

        fun electricityServiceTl(ctx: Context, serviceId: UUID) =
            defaultServiceTl(
                serviceName = ctx.resources.getString(R.string.service_electricity),
                measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.kWh_unit),
                serviceId = serviceId
            )

        fun gasServiceTl(ctx: Context, serviceId: UUID) =
            defaultServiceTl(
                serviceName = ctx.resources.getString(R.string.service_gas),
                measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
                serviceId = serviceId
            )

        fun coldWaterServiceTl(ctx: Context, serviceId: UUID) =
            defaultServiceTl(
                serviceName = ctx.resources.getString(R.string.service_cold_water),
                measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
                serviceId = serviceId
            )

        fun wasteServiceTl(ctx: Context, serviceId: UUID) =
            defaultServiceTl(
                serviceName = ctx.resources.getString(R.string.service_waste),
                measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
                serviceId = serviceId
            )

        fun heatingServiceTl(ctx: Context, serviceId: UUID) =
            defaultServiceTl(
                serviceName = ctx.resources.getString(R.string.service_heating),
                measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.Gcal_unit),
                serviceId = serviceId
            )

        fun hotWaterServiceTl(ctx: Context, serviceId: UUID) =
            defaultServiceTl(
                serviceName = ctx.resources.getString(R.string.service_hot_water),
                measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
                serviceId = serviceId
            )

        fun garbageServiceTl(ctx: Context, serviceId: UUID) =
            defaultServiceTl(
                serviceName = ctx.resources.getString(R.string.service_garbage),
                serviceId = serviceId
            )

        fun doorphoneServiceTl(ctx: Context, serviceId: UUID) =
            defaultServiceTl(
                serviceName = ctx.resources.getString(R.string.service_doorphone),
                serviceId = serviceId
            )

        fun phoneServiceTl(ctx: Context, serviceId: UUID) =
            defaultServiceTl(
                serviceName = ctx.resources.getString(R.string.service_phone),
                serviceId = serviceId
            )

        fun ugsoServiceTl(ctx: Context, serviceId: UUID) =
            defaultServiceTl(
                serviceName = ctx.resources.getString(R.string.service_ugso),
                serviceId = serviceId
            )

        fun internetServiceTl(ctx: Context, serviceId: UUID) =
            defaultServiceTl(
                serviceName = ctx.resources.getString(R.string.service_internet),
                serviceId = serviceId
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


