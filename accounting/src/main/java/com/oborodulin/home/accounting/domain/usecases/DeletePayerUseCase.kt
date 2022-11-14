package com.oborodulin.home.accounting.domain.usecases

import com.oborodulin.home.accounting.domain.model.Payer
import com.oborodulin.home.accounting.domain.repositories.PayersRepository
import com.oborodulin.home.common.domain.usecases.UseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeletePayerUseCase(
    configuration: Configuration,
    private val payersRepository: PayersRepository
) : UseCase<DeletePayerUseCase.Request, DeletePayerUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> {
        return payersRepository.delete(request.payer)
            .map {
                Response
            }
    }

    data class Request(val payer: Payer) : UseCase.Request
    object Response : UseCase.Response
}
