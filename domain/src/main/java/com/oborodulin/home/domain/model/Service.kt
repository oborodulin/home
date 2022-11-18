package com.oborodulin.home.domain.model

data class Service(
    var pos: Int,
    var name: String = "",
    var descr: String? = null,
    var isAllocateRate: Boolean = false,
    var tl: ServiceTl,
) : DomainModel()
