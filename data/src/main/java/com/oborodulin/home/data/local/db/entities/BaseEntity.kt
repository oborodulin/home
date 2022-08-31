package com.oborodulin.home.data.local.db.entities

import androidx.room.PrimaryKey
import java.util.UUID

open class BaseEntity(
    @PrimaryKey
    var id: UUID = UUID.randomUUID(),
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}