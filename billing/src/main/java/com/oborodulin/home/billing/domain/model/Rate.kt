package com.oborodulin.home.billing.domain.model

import com.oborodulin.home.domain.model.DomainModel
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.servicing.domain.model.Service
import java.math.BigDecimal
import java.time.OffsetDateTime

class Rate(
    val payer: Payer,
    val service: Service,
    val startDate: OffsetDateTime = OffsetDateTime.now(),
    val fromMeterValue: BigDecimal? = null,
    val toMeterValue: BigDecimal? = null,
    val rateValue: BigDecimal,
    val isPerPerson: Boolean = false, // считаем по норме на 1 человека, но приоритет для тарифов по счётчику
    val isPrivileges: Boolean = false // считаем по счётчику, но по льготному тарифу
) : DomainModel()