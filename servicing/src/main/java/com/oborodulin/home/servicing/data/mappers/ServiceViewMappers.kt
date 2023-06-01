package com.oborodulin.home.servicing.data.mappers

data class ServiceViewMappers(
    val serviceViewListToServiceListMapper: ServiceViewListToServiceListMapper,
    val serviceViewToServiceMapper: ServiceViewToServiceMapper,
    val serviceToServiceViewMapper: ServiceToServiceViewMapper,
    val payerServiceViewToServiceMapper: PayerServiceViewToServiceMapper,
    val payerServiceViewListToPayerServiceListMapper: PayerServiceViewListToPayerServiceListMapper
)
