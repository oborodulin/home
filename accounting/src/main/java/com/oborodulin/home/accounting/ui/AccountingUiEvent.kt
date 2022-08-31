package com.oborodulin.home.accounting.ui

import java.math.BigDecimal

sealed class AccountingUiEvent {
    data class ErcCodeChanged(val ercCode: String) : AccountingUiEvent()
    data class FullNameChanged(val fullName: String) : AccountingUiEvent()
    data class AddressChanged(val address: String) : AccountingUiEvent()
    data class TotalAreaChanged(val totalArea: BigDecimal?) : AccountingUiEvent()
    data class LivingSpaceChanged(val livingSpace: BigDecimal?) : AccountingUiEvent()
    data class HeatedVolumeChanged(val heatedVolume: BigDecimal?) : AccountingUiEvent()
    data class PaymentDayChanged(val paymentDay: Int?) : AccountingUiEvent()
    data class PersonsNumChanged(val personsNum: Int?) : AccountingUiEvent()
    object Submit : AccountingUiEvent()
}
