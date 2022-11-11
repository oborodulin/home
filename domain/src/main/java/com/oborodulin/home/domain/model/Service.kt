package com.oborodulin.home.domain.model

import com.oborodulin.home.common.domain.model.DomainModel

data class Service(
    var pos: Int,
    var name: String = "",
    var descr: String? = null,
    var isAllocateRate: Boolean = false
) : DomainModel()
