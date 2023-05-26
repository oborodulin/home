package com.oborodulin.home.billing.ui

import java.math.BigDecimal

sealed class BillingUiEvent {
    data class ErcCodeChanged(val ercCode: String) : BillingUiEvent()
    data class FullNameChanged(val fullName: String) : BillingUiEvent()
    data class AddressChanged(val address: String) : BillingUiEvent()
    data class TotalAreaChanged(val totalArea: BigDecimal?) : BillingUiEvent()
    data class LivingSpaceChanged(val livingSpace: BigDecimal?) : BillingUiEvent()
    data class HeatedVolumeChanged(val heatedVolume: BigDecimal?) : BillingUiEvent()
    data class PaymentDayChanged(val paymentDay: Int?) : BillingUiEvent()
    data class PersonsNumChanged(val personsNum: Int?) : BillingUiEvent()
    object Submit : BillingUiEvent()
}
