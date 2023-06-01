package com.oborodulin.home.domain.model

import com.oborodulin.home.common.domain.model.DomainModel
import java.math.BigDecimal

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
) : DomainModel()
