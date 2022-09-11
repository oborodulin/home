package com.oborodulin.home.data.local.db.entities

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = LanguageEntity.TABLE_NAME,
    indices = [Index(value = ["localeCode"], unique = true)]
)
class LanguageEntity(
    var localeCode: String,
    var name: String
) : BaseEntity() {
    companion object {
        const val TABLE_NAME = "languages"
    }
}