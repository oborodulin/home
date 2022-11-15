package com.oborodulin.home.domain.usecase

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.domain.repositories.PayersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SavePayerUseCase(
    configuration: Configuration,
    private val payersRepository: PayersRepository
) : UseCase<SavePayerUseCase.Request, SavePayerUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> {
        return payersRepository.save(request.payer)
            .map {
                Response
            }
    }

    data class Request(val payer: Payer) : UseCase.Request
    object Response : UseCase.Response
}
