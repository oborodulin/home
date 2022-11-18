package com.oborodulin.home.accounting.ui.payer.single

import com.oborodulin.home.accounting.ui.model.PayerModel
import com.oborodulin.home.common.ui.state.UiAction
import java.math.BigDecimal
import java.util.*

sealed class PayerUiAction : UiAction {
    data class Load(val payerId: UUID) : PayerUiAction()
    data class ChangeErcCode(val newErcCode: String) : PayerUiAction()
    data class ChangeFullName(val newFullName: String) : PayerUiAction()
    data class ChangeAddress(val newAddress: String) : PayerUiAction()
    data class ChangeTotalArea(val newTotalArea: BigDecimal?) : PayerUiAction()
    data class ChangeLivingSpace(val newLivingSpace: BigDecimal?) : PayerUiAction()
    data class ChangeHeatedVolume(val newHeatedVolume: BigDecimal?) : PayerUiAction()
    data class ChangePaymentDay(val newPaymentDay: Int?) : PayerUiAction()
    data class ChangePersonsNum(val newPersonsNum: Int?) : PayerUiAction()
    data class Save(val payerModel: PayerModel) : PayerUiAction()
//    data class ShowCompletedTasks(val show: Boolean) : PayersListEvent()
//    data class ChangeSortByPriority(val enable: Boolean) : PayersListEvent()
//    data class ChangeSortByDeadline(val enable: Boolean) : PayersListEvent()
}