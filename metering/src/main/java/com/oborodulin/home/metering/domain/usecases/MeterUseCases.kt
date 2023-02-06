package com.oborodulin.home.metering.domain.usecases

data class MeterUseCases(
    val getMetersUseCase: GetMetersUseCase,
    val getPrevServiceMeterValuesUseCase: GetPrevServiceMeterValuesUseCase,
    val deleteMeterValueUseCase: DeleteMeterValueUseCase,
    val saveMeterValueUseCase: SaveMeterValueUseCase
)
