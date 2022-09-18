package com.oborodulin.home.data.local.db.entities

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = LanguageEntity.TABLE_NAME,
    indices = [Index(value = ["localeCode"], unique = true)]
)
class LanguageEntity(
    val localeCode: String,
    val name: String
) : BaseEntity() {
    companion object {
        const val TABLE_NAME = "languages"
    }
}