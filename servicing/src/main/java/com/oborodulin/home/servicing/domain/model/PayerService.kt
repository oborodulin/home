package com.oborodulin.home.servicing.domain.model

import com.oborodulin.home.domain.model.DomainModel
import com.oborodulin.home.domain.model.Payer

data class PayerService(
    val payer: Payer = Payer(),
    val services: MutableList<Service> = mutableListOf()
) : DomainModel()
