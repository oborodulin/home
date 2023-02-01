package com.oborodulin.home.metering.domain.usecases

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.domain.repositories.PayersRepository
import com.oborodulin.home.metering.domain.model.MeterValue
import com.oborodulin.home.metering.domain.repositories.MetersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SaveMeterValueUseCase(
    configuration: Configuration,
    private val metersRepository: MetersRepository
) : UseCase<SaveMeterValueUseCase.Request, SaveMeterValueUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> {
        return metersRepository.save(request.meterValue)
            .map {
                Response(it)
            }
    }

    data class Request(val meterValue: MeterValue) : UseCase.Request
    data class Response(val meterValue: MeterValue) : UseCase.Response
}
