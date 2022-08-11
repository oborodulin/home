package com.oborodulin.home.domain.entity

import androidx.room.Entity
import androidx.room.Index
import com.oborodulin.home.domain.entity.BaseEntity

@Entity(tableName = "services", indices = [Index(value = ["displayName"], unique = true)])
class Service(
    var displayPos: Int,
    var displayName: String = "",
    var serviceDescr: String? = null,
    var isAllocateRate: Boolean = false,
) : BaseEntity()
