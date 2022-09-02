package com.oborodulin.home.accounting.domain.usecases

import com.oborodulin.home.accounting.domain.repositories.PayersRepository

class GetPayers(private val payersRepository: PayersRepository) {
    operator fun invoke() = payersRepository.getAll()
}