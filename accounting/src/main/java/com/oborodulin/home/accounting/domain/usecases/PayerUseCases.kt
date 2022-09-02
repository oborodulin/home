package com.oborodulin.home.accounting.domain.usecases

data class PayerUseCases(
    val getPayer: GetPayer,
    val getPayers: GetPayers,
    val savePayer: SavePayer,
    val deletePayer: DeletePayer
)
