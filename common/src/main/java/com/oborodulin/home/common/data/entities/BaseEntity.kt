package com.oborodulin.home.common.data.entities

import java.util.UUID

abstract class BaseEntity() {
    abstract fun id(): UUID

    open fun key(): Int {
        // unique indices
        return id().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass.asSubclass(this.javaClass) != other?.javaClass?.asSubclass(this.javaClass)) return false

        other as BaseEntity
        if (id() != other.id() || key() != other.key()) return false

        return true
    }

    override fun hashCode(): Int {
        return id().hashCode()
    }
}

