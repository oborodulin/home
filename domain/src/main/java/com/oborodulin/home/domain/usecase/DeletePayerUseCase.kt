package com.oborodulin.home.domain.usecase

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.domain.repositories.PayersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class DeletePayerUseCase(
    configuration: Configuration,
    private val payersRepository: PayersRepository
) : UseCase<DeletePayerUseCase.Request, DeletePayerUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> {
        return payersRepository.deleteById(request.payerId)
            .map {
                Response
            }
    }

    data class Request(val payerId: UUID) : UseCase.Request
    object Response : UseCase.Response
}
