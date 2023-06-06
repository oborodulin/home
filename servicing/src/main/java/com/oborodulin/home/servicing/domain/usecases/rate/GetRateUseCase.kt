package com.oborodulin.home.servicing.domain.usecases.rate

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.servicing.domain.model.Rate
import com.oborodulin.home.servicing.domain.repositories.RatesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class GetRateUseCase(
    configuration: Configuration,
    private val ratesRepository: RatesRepository
) : UseCase<GetRateUseCase.Request, GetRateUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> =
        ratesRepository.get(request.id).map {
            Response(it)
        }

    data class Request(val id: UUID) : UseCase.Request
    data class Response(val rate: Rate) : UseCase.Response
}