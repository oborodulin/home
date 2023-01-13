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
        onDelete = ForeignKey.CASCADE
    )]
)
class ServiceTlEntity(
    @PrimaryKey var serviceTlId: UUID = UUID.randomUUID(),
    val localeCode: String = Locale.getDefault().language,
    val name: String = "",
    var measureUnit: String? = null,
    val descr: String? = null,
    @ColumnInfo(index = true) var servicesId: UUID? = null,
) {
    companion object {
        const val TABLE_NAME = "services_tl"

        fun populateRentServiceTl(ctx: Context) =
            ServiceTlEntity(name = ctx.resources.getString(R.string.service_rent))

        fun populateElectricityServiceTl(ctx: Context) =
            ServiceTlEntity(
                name = ctx.resources.getString(R.string.service_electricity),
                measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.kWh_unit)
            )

        fun populateGasServiceTl(ctx: Context) =
            ServiceTlEntity(
                name = ctx.resources.getString(R.string.service_gas),
                measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit)
            )

        fun populateColdWaterServiceTl(ctx: Context) =
            ServiceTlEntity(
                name = ctx.resources.getString(R.string.service_cold_water),
                measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit)
            )

        fun populateWasteServiceTl(ctx: Context) =
            ServiceTlEntity(
                name = ctx.resources.getString(R.string.service_waste),
                measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit)
            )

        fun populateHeatingServiceTl(ctx: Context) =
            ServiceTlEntity(
                name = ctx.resources.getString(R.string.service_heating),
                measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.Gcal_unit)
            )

        fun populateHotWaterServiceTl(ctx: Context) =
            ServiceTlEntity(
                name = ctx.resources.getString(R.string.service_hot_water),
                measureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit)
            )

        fun populateGarbageServiceTl(ctx: Context) =
            ServiceTlEntity(
                name = ctx.resources.getString(R.string.service_garbage)
            )

        fun populateDoorphoneServiceTl(ctx: Context) =
            ServiceTlEntity(
                name = ctx.resources.getString(R.string.service_doorphone)
            )

        fun populatePhoneServiceTl(ctx: Context) =
            ServiceTlEntity(
                name = ctx.resources.getString(R.string.service_phone)
            )

        fun populateUgsoServiceTl(ctx: Context) =
            ServiceTlEntity(
                name = ctx.resources.getString(R.string.service_ugso)
            )
    }
}


