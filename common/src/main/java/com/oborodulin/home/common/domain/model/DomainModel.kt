package com.oborodulin.home.common.domain.model;

import java.io.Serializable
import java.util.UUID

open class DomainModel(
    var id: UUID? = null,
) : Serializable

