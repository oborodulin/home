package com.oborodulin.home.servicing.domain.usecases.rate

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.common.domain.usecases.UseCaseException
import com.oborodulin.home.servicing.domain.model.Rate
import com.oborodulin.home.servicing.domain.repositories.RatesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class SaveRateUseCase(
    configuration: Configuration,
    private val ratesRepository: RatesRepository
) : UseCase<SaveRateUseCase.Request, SaveRateUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> {
        return ratesRepository.save(request.rate)
            .map {
                Response(it)
            }.catch { throw UseCaseException.RateSaveException(it) }
    }

    data class Request(val rate: Rate) : UseCase.Request
    data class Response(val rate: Rate) : UseCase.Response
}
