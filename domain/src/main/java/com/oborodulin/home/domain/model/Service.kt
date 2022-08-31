package com.oborodulin.home.domain.model

import java.util.*

data class Service(
    var id: UUID? = null,
    var pos: Int,
    var name: String = "",
    var desc: String? = null,
    var isAllocateRate: Boolean = false,
) : DomainModel()
