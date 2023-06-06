package com.oborodulin.home.servicing.data.mappers

data class ServiceMappers(
    val serviceViewListToServiceListMapper: ServiceViewListToServiceListMapper,
    val serviceViewToServiceMapper: ServiceViewToServiceMapper,
    val serviceToServiceViewMapper: ServiceToServiceViewMapper,
    val serviceToServiceEntityMapper: ServiceToServiceEntityMapper,
    val serviceToServiceTlEntityMapper: ServiceToServiceTlEntityMapper
)
