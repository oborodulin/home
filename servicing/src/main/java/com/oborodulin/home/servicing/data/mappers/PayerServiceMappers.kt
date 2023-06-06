package com.oborodulin.home.servicing.data.mappers

data class PayerServiceMappers(
    val payerServiceViewToPayerServiceMapper: PayerServiceViewToPayerServiceMapper,
    val payerServiceViewListToPayerServiceListMapper: PayerServiceViewListToPayerServiceListMapper,
    val payerServiceToPayerServiceCrossRefEntityMapper: PayerServiceToPayerServiceCrossRefEntityMapper
)
