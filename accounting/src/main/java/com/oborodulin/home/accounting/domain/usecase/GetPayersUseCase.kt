package com.oborodulin.home.accounting.domain.usecase

import com.oborodulin.home.accounting.domain.repositories.PayersRepository

class GetPayersUseCase(private val payersRepository: PayersRepository) {
    suspend operator fun invoke() = payersRepository.getAll()
}