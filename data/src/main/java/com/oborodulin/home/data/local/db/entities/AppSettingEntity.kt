package com.oborodulin.home.data.local.db.entities

import android.content.Context
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.oborodulin.home.common.util.Utils.Companion.currencySymbol
import java.util.*

@Entity(
    tableName = AppSettingEntity.TABLE_NAME,
    indices = [Index(value = ["paramName"], unique = true)]
)
class AppSettingEntity(
    @PrimaryKey val settingId: UUID = UUID.randomUUID(),
    val paramName: String = "",
    val paramValue: String = ""
) {
    companion object {
        const val TABLE_NAME = "app_settings"

        const val PARAM_LANG = "LANG"
        const val PARAM_CURRENCY = "CURRENCY"
        const val PARAM_TOTAL_AREA_MU = "TOTAL_AREA_MU"
        const val PARAM_LIVING_SPACE_MU = "LIVING_SPACE_MU"
        const val PARAM_HEATED_VOLUME_MU = "HEATED_VOLUME_MU"

        fun defaultParam(
            settingId: UUID = UUID.randomUUID(),
            paramName: String = "",
            paramValue: String = ""
        ) = AppSettingEntity(settingId = settingId, paramName = paramName, paramValue = paramValue)

        fun langParam() = defaultParam(
            paramName = PARAM_LANG,
            paramValue = Locale.getDefault().language
        )

        fun currencyParam() = defaultParam(
            paramName = PARAM_CURRENCY,
            paramValue = currencySymbol()!!
        )

        fun totalAreaMuParam(ctx: Context) = defaultParam(
            paramName = PARAM_TOTAL_AREA_MU,
            paramValue = ctx.resources.getString(com.oborodulin.home.common.R.string.m2_unit)
        )

        fun livingSpaceMuParam(ctx: Context) = defaultParam(
            paramName = PARAM_LIVING_SPACE_MU,
            paramValue = ctx.resources.getString(com.oborodulin.home.common.R.string.m2_unit)
        )

        fun heatedVolumeMuParam(ctx: Context) = defaultParam(
            paramName = PARAM_HEATED_VOLUME_MU,
            paramValue = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit)
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppSettingEntity
        if (settingId != other.settingId || paramName != other.paramName) return false

        return true
    }

    override fun hashCode(): Int {
        return settingId.hashCode()
    }
}