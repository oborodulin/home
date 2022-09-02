package com.oborodulin.home.accounting.domain.usecases

import com.oborodulin.home.accounting.domain.repositories.PayersRepository
import java.util.*

class GetPayer(private val payersRepository: PayersRepository) {
    operator fun invoke(id: UUID) = payersRepository.get(id)
}