package com.oborodulin.home.servicing.data.mappers

data class PayerServiceViewMappers(
    val serviceViewListToServiceListMapper: ServiceViewListToServiceListMapper,
    val serviceViewToServiceMapper: ServiceViewToServiceMapper,
    val serviceToServiceViewMapper: ServiceToServiceViewMapper,
    val payerServiceViewToServiceMapper: PayerServiceViewToServiceMapper,
    val payerServiceViewListToPayerServiceListMapper: PayerServiceViewListToPayerServiceListMapper
)
