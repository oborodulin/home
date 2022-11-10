package com.oborodulin.home.accounting.domain.usecases

import com.oborodulin.home.accounting.domain.model.Payer
import com.oborodulin.home.accounting.domain.repositories.PayersRepository
import com.oborodulin.home.common.domain.usecases.UseCase

class DeletePayerUseCase(
    configuration: UseCase.Configuration,
    private val payersRepository: PayersRepository) {
    suspend operator fun invoke(payer: Payer) = payersRepository.delete(payer)
}