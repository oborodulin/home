package com.oborodulin.home.domain.model;

import java.io.Serializable
import java.util.UUID

open class DomainModel(
    var id: UUID = UUID.randomUUID(),
) : Serializable

