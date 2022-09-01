package com.oborodulin.home.domain.model

import java.util.*

data class Service(
    var pos: Int,
    var name: String = "",
    var descr: String? = null,
    var isAllocateRate: Boolean = false,
) : DomainModel()
