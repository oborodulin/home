package com.oborodulin.home.data.local.db.entities

import android.content.Context
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
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

        fun populateLocalParam(ctx: Context) =
            AppSettingEntity(
                paramName = "PARAM_LANG",
                paramValue = "ru"
            )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppSettingEntity
        if (settingId != other.settingId) return false

        return true
    }

    override fun hashCode(): Int {
        return settingId.hashCode()
    }
}