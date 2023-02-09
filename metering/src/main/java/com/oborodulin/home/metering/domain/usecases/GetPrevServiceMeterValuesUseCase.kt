package com.oborodulin.home.metering.domain.usecases

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.data.local.db.views.MeterValuePrevPeriodsView
import com.oborodulin.home.metering.domain.repositories.MetersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class GetPrevServiceMeterValuesUseCase(
    configuration: Configuration,
    private val metersRepository: MetersRepository
) : UseCase<GetPrevServiceMeterValuesUseCase.Request, GetPrevServiceMeterValuesUseCase.Response>(
    configuration
) {
    override fun process(request: Request): Flow<Response> =
        metersRepository.getPrevServiceMeterValues(request.payerId).map {
            Response(it)
        }

    data class Request(val payerId: UUID?) : UseCase.Request
    data class Response(val prevServiceMeterValues: List<MeterValuePrevPeriodsView>) :
        UseCase.Response
}