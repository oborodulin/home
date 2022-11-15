package com.oborodulin.home.domain.usecase

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.domain.repositories.PayersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class GetPayerUseCase(
    configuration: Configuration,
    private val payersRepository: PayersRepository
) : UseCase<GetPayerUseCase.Request, GetPayerUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> =
        payersRepository.get(request.id).map {
            Response(it)
        }

    data class Request(val id: UUID) : UseCase.Request
    data class Response(val payer: Payer) : UseCase.Response
}