package com.oborodulin.home.servicing.domain.usecases.rate

data class RateUseCases(
    val getRateUseCase: GetRateUseCase,
    val getRatesUseCase: GetRatesUseCase,
    val saveRateUseCase: SaveRateUseCase,
    val deleteRateUseCase: DeleteRateUseCase
)
