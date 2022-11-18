package com.oborodulin.home.domain.model;

import java.io.Serializable
import java.util.*

open class DomainModel(
    var id: UUID = UUID.randomUUID(),
) : Serializable

