package com.oborodulin.home.accounting.ui.payer

import java.math.BigDecimal

sealed class PayerDetailEvent {
    data class ChangeErcCode(val newErcCode: String) : PayerDetailEvent()
    data class ChangeFullName(val newFullName: String) : PayerDetailEvent()
    data class ChangeAddress(val newAddress: String) : PayerDetailEvent()
    data class ChangeTotalArea(val newTotalArea: BigDecimal?) : PayerDetailEvent()
    data class ChangeLivingSpace(val newLivingSpace: BigDecimal?) : PayerDetailEvent()
    data class ChangeHeatedVolume(val newHeatedVolume: BigDecimal?) : PayerDetailEvent()
    data class ChangePaymentDay(val newPaymentDay: Int?) : PayerDetailEvent()
    data class ChangePersonsNum(val newPersonsNum: Int?) : PayerDetailEvent()
    object SavePayer : PayerDetailEvent()
}
