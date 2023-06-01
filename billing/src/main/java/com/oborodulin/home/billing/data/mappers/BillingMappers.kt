package com.oborodulin.home.billing.data.mappers

data class BillingMappers(
    val payerServiceSubtotalDebtViewListToPayerServiceDebtListMapper: PayerServiceSubtotalDebtViewListToPayerServiceDebtListMapper,
    val payerTotalDebtViewToPayerDebtMapper: PayerTotalDebtViewToPayerDebtMapper,
    val payerTotalDebtViewListToPayerDebtListMapper: PayerTotalDebtViewListToPayerDebtListMapper
)
