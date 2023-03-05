package com.oborodulin.home.domain.model

import java.math.BigDecimal
import java.time.OffsetDateTime

data class Payer(
    val ercCode: String = "",
    val fullName: String = "",
    val address: String = "",
    val totalArea: BigDecimal? = null,
    val livingSpace: BigDecimal? = null,
    val heatedVolume: BigDecimal? = null,
    val paymentDay: Int = 20,
    val personsNum: Int = 1,
    val isAlignByPaymentDay: Boolean = false,
    val isFavorite: Boolean = false,
    var fromPaymentDate: OffsetDateTime? = null,
    var toPaymentDate: OffsetDateTime? = null,
    var totalDebt: BigDecimal = BigDecimal.ZERO
) : DomainModel()
