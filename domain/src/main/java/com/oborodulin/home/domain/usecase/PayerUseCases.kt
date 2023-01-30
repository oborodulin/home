package com.oborodulin.home.domain.usecase

data class PayerUseCases(
    val getPayerUseCase: GetPayerUseCase,
    val getPayersUseCase: GetPayersUseCase,
    val savePayerUseCase: SavePayerUseCase,
    val deletePayerUseCase: DeletePayerUseCase,
    val favoritePayerUseCase: FavoritePayerUseCase
)
