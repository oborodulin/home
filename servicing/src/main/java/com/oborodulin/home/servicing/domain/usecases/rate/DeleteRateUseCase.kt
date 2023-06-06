package com.oborodulin.home.servicing.domain.usecases.rate

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.servicing.domain.repositories.RatesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class DeleteRateUseCase(
    configuration: Configuration,
    private val ratesRepository: RatesRepository
) : UseCase<DeleteRateUseCase.Request, DeleteRateUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> {
        return ratesRepository.delete(request.rateId)
            .map {
                Response
            }
    }

    data class Request(val rateId: UUID) : UseCase.Request
    object Response : UseCase.Response
}
