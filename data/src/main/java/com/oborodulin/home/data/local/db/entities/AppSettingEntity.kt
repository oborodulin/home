package com.oborodulin.home.data.local.db.entities

import android.content.Context
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.oborodulin.home.common.data.entities.BaseEntity
import com.oborodulin.home.common.util.Utils.Companion.currencyCode
import com.oborodulin.home.domain.util.AppSettingParam
import java.util.Locale
import java.util.UUID

@Entity(
    tableName = AppSettingEntity.TABLE_NAME,
    indices = [Index(value = ["paramName"], unique = true)]
)
data class AppSettingEntity(
    @PrimaryKey val settingId: UUID = UUID.randomUUID(),
    val paramName: AppSettingParam,
    val paramValue: String = ""
) : BaseEntity() {

    companion object {
        const val TABLE_NAME = "app_settings"

        fun defaultParam(
            settingId: UUID = UUID.randomUUID(),
            paramName: AppSettingParam,
            paramValue: String = ""
        ) = AppSettingEntity(settingId = settingId, paramName = paramName, paramValue = paramValue)

        fun langParam() = defaultParam(
            paramName = AppSettingParam.LANG,
            paramValue = Locale.getDefault().language
        )

        fun currencyCodeParam() = defaultParam(
            paramName = AppSettingParam.CURRENCY_CODE,
            paramValue = currencyCode()
        )

        fun dayMuParam(ctx: Context) = defaultParam(
            paramName = AppSettingParam.DAY_MU,
            paramValue = ctx.resources.getString(com.oborodulin.home.common.R.string.day_unit)
        )

        fun monthMuParam(ctx: Context) = defaultParam(
            paramName = AppSettingParam.MONTH_MU,
            paramValue = ctx.resources.getString(com.oborodulin.home.common.R.string.month_unit)
        )

        fun personNumMuParam(ctx: Context) = defaultParam(
            paramName = AppSettingParam.PERSON_NUM_MU,
            paramValue = ctx.resources.getString(com.oborodulin.home.common.R.string.person_unit)
        )

        fun totalAreaMuParam(ctx: Context) = defaultParam(
            paramName = AppSettingParam.TOTAL_AREA_MU,
            paramValue = ctx.resources.getString(com.oborodulin.home.common.R.string.m2_unit)
        )

        fun livingSpaceMuParam(ctx: Context) = defaultParam(
            paramName = AppSettingParam.LIVING_SPACE_MU,
            paramValue = ctx.resources.getString(com.oborodulin.home.common.R.string.m2_unit)
        )

        fun heatedVolumeMuParam(ctx: Context) = defaultParam(
            paramName = AppSettingParam.HEATED_VOLUME_MU,
            paramValue = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit)
        )
    }

    override fun id() = this.settingId

    override fun key() = paramName.hashCode()

    override fun toString(): String {
        val str = StringBuffer()
        str.append("AppSetting Entity: ").append(paramName).append(" = ").append("'")
            .append(paramValue)
            .append("' settingId = ").append(settingId)
        return str.toString()
    }
}