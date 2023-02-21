package com.oborodulin.home.accounting.domain.usecases

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.domain.repositories.PayersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFavoritePayerUseCase(
    configuration: Configuration,
    private val payersRepository: PayersRepository
) : UseCase<GetFavoritePayerUseCase.Request, GetFavoritePayerUseCase.Response>(configuration) {

    override fun process(request: Request): Flow<Response> =
        payersRepository.getFavorite().map {
            Response(it)
        }

    object Request : UseCase.Request
    data class Response(val payer: Payer) : UseCase.Response
}