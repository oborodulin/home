package com.oborodulin.home.domain.usecase

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.domain.repositories.PayersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPayersUseCase(
    configuration: Configuration,
    private val payersRepository: PayersRepository
) : UseCase<GetPayersUseCase.Request, GetPayersUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> =
        payersRepository.getAll().map {
            Response(it)
        }

    object Request : UseCase.Request
    data class Response(val payers: List<Payer>) : UseCase.Response
}