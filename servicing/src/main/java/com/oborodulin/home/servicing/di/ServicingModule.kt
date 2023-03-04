package com.oborodulin.home.servicing.di

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.data.local.db.dao.ServiceDao
import com.oborodulin.home.servicing.data.mappers.*
import com.oborodulin.home.servicing.data.repositories.ServicesRepositoryImp
import com.oborodulin.home.servicing.data.repositories.ServicingDataSource
import com.oborodulin.home.servicing.data.repositories.ServicingDataSourceImp
import com.oborodulin.home.servicing.domain.repositories.ServicesRepository
import com.oborodulin.home.servicing.domain.usecases.GetServicesUseCase
import com.oborodulin.home.servicing.domain.usecases.ServicingUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServicingModule {
    // DATA MAPPERS:
    @Singleton
    @Provides
    fun provideServiceViewToServiceMapper(): ServiceViewToServiceMapper =
        ServiceViewToServiceMapper()

    @Singleton
    @Provides
    fun provideServiceViewListToServiceListMapper(mapper: ServiceViewToServiceMapper): ServiceViewListToServiceListMapper =
        ServiceViewListToServiceListMapper(mapper = mapper)

    @Singleton
    @Provides
    fun provideServiceToServiceEntityMapper(): ServiceToServiceEntityMapper =
        ServiceToServiceEntityMapper()

    @Singleton
    @Provides
    fun provideServiceToServiceTlEntityMapper(): ServiceToServiceTlEntityMapper =
        ServiceToServiceTlEntityMapper()

    @Singleton
    @Provides
    fun providePayerServiceViewToServiceMapper(mapper: ServiceViewToServiceMapper): PayerServiceViewToServiceMapper =
        PayerServiceViewToServiceMapper(mapper = mapper)

    @Singleton
    @Provides
    fun providePayerServiceViewListToServiceListMapper(mapper: PayerServiceViewToServiceMapper): PayerServiceViewListToServiceListMapper =
        PayerServiceViewListToServiceListMapper(mapper = mapper)

    // UI MAPPERS:

    // CONVERTERS:

    // DATA SOURCES:
    @Singleton
    @Provides
    fun provideServicingDataSourceImp(
        serviceDao: ServiceDao,
        @IoDispatcher dispatcher: CoroutineDispatcher,
        serviceViewListToServiceListMapper: ServiceViewListToServiceListMapper,
        serviceViewToServiceMapper: ServiceViewToServiceMapper,
        payerServiceViewToServiceMapper: PayerServiceViewToServiceMapper,
        payerServiceViewListToServiceListMapper: PayerServiceViewListToServiceListMapper,
        serviceToServiceEntityMapper: ServiceToServiceEntityMapper,
        serviceToServiceTlEntityMapper: ServiceToServiceTlEntityMapper
    ): ServicingDataSource =
        ServicingDataSourceImp(
            serviceDao,
            dispatcher,
            serviceViewListToServiceListMapper,
            serviceViewToServiceMapper,
            payerServiceViewToServiceMapper,
            payerServiceViewListToServiceListMapper,
            serviceToServiceEntityMapper,
            serviceToServiceTlEntityMapper
        )

    // REPOSITORIES:
    @Singleton
    @Provides
    fun provideServicesRepository(servicingDataSource: ServicingDataSource): ServicesRepository =
        ServicesRepositoryImp(servicingDataSource)

    // USE CASES:
    @Singleton
    @Provides
    fun providePayerServiceUseCases(
        configuration: UseCase.Configuration, repository: ServicesRepository
    ): ServicingUseCases =
        ServicingUseCases(
            getServicesUseCase = GetServicesUseCase(
                configuration,
                repository
            )
        )
}