package com.oborodulin.home.accounting.ui.model.converters

import com.oborodulin.home.accounting.ui.model.PayerModel
import com.oborodulin.home.common.ui.state.CommonResultConverter
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.domain.usecase.GetPayerUseCase
import com.oborodulin.home.domain.usecase.SavePayerUseCase

class PayerConverter :
    CommonResultConverter<GetPayerUseCase.Response, PayerModel>() {

    override fun convertSuccess(data: GetPayerUseCase.Response) =
        PayerModel(
            id = data.payer.id,
            ercCode = data.payer.ercCode,
            fullName = data.payer.fullName,
            address = data.payer.address,
            totalArea = data.payer.totalArea,
            livingSpace = data.payer.livingSpace,
            heatedVolume = data.payer.heatedVolume,
            paymentDay = data.payer.paymentDay,
            personsNum = data.payer.personsNum,
            isFavorite = data.payer.isFavorite
        )

    fun toPayer(payerModel: PayerModel): Payer {
        val payer = Payer(
            ercCode = payerModel.ercCode,
            fullName = payerModel.fullName,
            address = payerModel.address,
            totalArea = payerModel.totalArea,
            livingSpace = payerModel.livingSpace,
            heatedVolume = payerModel.heatedVolume,
            paymentDay = payerModel.paymentDay,
            personsNum = payerModel.personsNum,
            isFavorite = payerModel.isFavorite
        )
        payer.id = payerModel.id
        return payer
    }
}