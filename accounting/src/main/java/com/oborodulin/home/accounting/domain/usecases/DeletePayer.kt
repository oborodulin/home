package com.oborodulin.home.accounting.domain.usecases

import com.oborodulin.home.accounting.domain.model.Payer
import com.oborodulin.home.accounting.domain.repositories.PayersRepository
import java.util.*

class DeletePayer(private val payersRepository: PayersRepository) {
    suspend operator fun invoke(payer: Payer) = payersRepository.delete(payer)
}