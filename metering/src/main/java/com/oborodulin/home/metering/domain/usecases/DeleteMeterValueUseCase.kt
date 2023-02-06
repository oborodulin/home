package com.oborodulin.home.metering.domain.usecases

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.metering.domain.repositories.MetersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class DeleteMeterValueUseCase(
    configuration: Configuration,
    private val metersRepository: MetersRepository
) : UseCase<DeleteMeterValueUseCase.Request, DeleteMeterValueUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> {
        return metersRepository.deleteCurrentValue(request.meterId)
            .map {
                Response
            }
    }

    data class Request(val meterId: UUID) : UseCase.Request
    object Response : UseCase.Response
}
