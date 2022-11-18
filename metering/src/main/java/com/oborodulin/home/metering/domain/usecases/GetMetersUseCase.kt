package com.oborodulin.home.metering.domain.usecases

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.metering.domain.model.Meter
import com.oborodulin.home.metering.domain.repositories.MetersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class GetMetersUseCase(
    configuration: Configuration,
    private val metersRepository: MetersRepository
) : UseCase<GetMetersUseCase.Request, GetMetersUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> =
        metersRepository.getMeters(request.payerId).map { list ->
            list.map { meter ->
                metersRepository.getMeterValues(meter.id).collect {
                    meter.meterValues = it
                }
                metersRepository.getMeterVerifications(meter.id).collect {
                    meter.meterVerifications = it
                }
                meter
            }
        }.map {
            Response(it)
        }

    data class Request(val payerId: UUID, val serviceId: UUID) : UseCase.Request
    data class Response(val meters: List<Meter>) : UseCase.Response
}