package com.oborodulin.home.servicing.domain.usecases.rate

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.servicing.domain.model.Rate
import com.oborodulin.home.servicing.domain.repositories.RatesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetRatesUseCase(
    configuration: Configuration,
    private val ratesRepository: RatesRepository
) : UseCase<GetRatesUseCase.Request, GetRatesUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> =
        ratesRepository.getAll().map {
            Response(it)
        }

    object Request : UseCase.Request
    data class Response(val rates: List<Rate>) : UseCase.Response
}