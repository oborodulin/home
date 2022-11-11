package com.oborodulin.home.accounting.domain.usecases

data class PayerUseCases(
    val getPayerUseCase: GetPayerUseCase,
    val getPayersUseCase: GetPayersUseCase,
    val savePayerUseCase: SavePayerUseCase,
    val deletePayerUseCase: DeletePayerUseCase
)
