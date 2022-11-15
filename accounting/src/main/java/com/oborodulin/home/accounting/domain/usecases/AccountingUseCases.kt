package com.oborodulin.home.accounting.domain.usecases

import com.oborodulin.home.metering.domain.usecases.GetPrevServiceMeterValuesUseCase

data class AccountingUseCases(
    val getPrevServiceMeterValuesUseCase: GetPrevServiceMeterValuesUseCase,
)
