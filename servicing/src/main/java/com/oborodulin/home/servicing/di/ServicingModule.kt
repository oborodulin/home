package com.oborodulin.home.servicing.di

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.dao.ServiceDao
import com.oborodulin.home.servicing.data.mappers.*
import com.oborodulin.home.servicing.data.repositories.ServicesRepositoryImpl
import com.oborodulin.home.servicing.data.repositories.sources.local.LocalServiceDataSource
import com.oborodulin.home.servicing.data.sources.local.LocalServiceDataSourceImpl
import com.oborodulin.home.servicing.domain.repositories.ServicesRepository
import com.oborodulin.home.servicing.domain.usecases.service.DeleteServiceUseCase
import com.oborodulin.home.servicing.domain.usecases.service.GetServiceUseCase
import com.oborodulin.home.servicing.domain.usecases.service.GetServicesUseCase
import com.oborodulin.home.servicing.domain.usecases.service.SaveServiceUseCase
import com.oborodulin.home.servicing.domain.usecases.service.ServiceUseCases
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
    // Services:
    @Singleton
    @Provides
    fun provideServiceViewToServiceEntityMapper(): ServiceViewToServiceEntityMapper =
        ServiceViewToServiceEntityMapper()

    @Singleton
    @Provides
    fun provideServiceViewToServiceTlEntityMapper(): ServiceViewToServiceTlEntityMapper =
        ServiceViewToServiceTlEntityMapper()

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
    fun provideServiceToServiceViewMapper(): ServiceToServiceViewMapper =
        ServiceToServiceViewMapper()

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
    fun provideServiceMappers(
        serviceViewListToServiceListMapper: ServiceViewListToServiceListMapper,
        serviceViewToServiceMapper: ServiceViewToServiceMapper,
        serviceToServiceViewMapper: ServiceToServiceViewMapper,
        serviceToServiceEntityMapper: ServiceToServiceEntityMapper,
        serviceToServiceTlEntityMapper: ServiceToServiceTlEntityMapper
    ): ServiceMappers = ServiceMappers(
        serviceViewListToServiceListMapper,
        serviceViewToServiceMapper,
        serviceToServiceViewMapper,
        serviceToServiceEntityMapper,
        serviceToServiceTlEntityMapper
    )

    //Payer Services:
    @Singleton
    @Provides
    fun providePayerServiceViewToServiceMapper(mapper: ServiceViewToServiceMapper): PayerServiceViewToPayerServiceMapper =
        PayerServiceViewToPayerServiceMapper(mapper = mapper)

    @Singleton
    @Provides
    fun providePayerServiceViewListToServiceListMapper(mapper: PayerServiceViewToPayerServiceMapper): PayerServiceViewListToPayerServiceListMapper =
        PayerServiceViewListToPayerServiceListMapper(mapper = mapper)

    @Singleton
    @Provides
    fun providePayerServiceToPayerServiceCrossRefEntityMapper(): PayerServiceToPayerServiceCrossRefEntityMapper =
        PayerServiceToPayerServiceCrossRefEntityMapper()

    @Singleton
    @Provides
    fun providePayerServiceMappers(
        payerServiceViewToPayerServiceMapper: PayerServiceViewToPayerServiceMapper,
        payerServiceViewListToPayerServiceListMapper: PayerServiceViewListToPayerServiceListMapper,
        payerServiceToPayerServiceCrossRefEntityMapper: PayerServiceToPayerServiceCrossRefEntityMapper
    ): PayerServiceMappers = PayerServiceMappers(
        payerServiceViewToPayerServiceMapper,
        payerServiceViewListToPayerServiceListMapper,
        payerServiceToPayerServiceCrossRefEntityMapper
    )
    // UI MAPPERS:

    // CONVERTERS:

    // DATA SOURCES:
    @Singleton
    @Provides
    fun provideServicingDataSourceImp(
        serviceDao: ServiceDao, payerDao: PayerDao,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): LocalServiceDataSource = LocalServiceDataSourceImpl(serviceDao, payerDao, dispatcher)

    // REPOSITORIES:
    @Singleton
    @Provides
    fun provideServicesRepository(
        localServiceDataSource: LocalServiceDataSource,
        serviceMappers: ServiceMappers,
        payerServiceMappers: PayerServiceMappers
    ): ServicesRepository =
        ServicesRepositoryImpl(localServiceDataSource, serviceMappers, payerServiceMappers)

    // USE CASES:
    @Singleton
    @Provides
    fun providePayerServiceUseCases(
        configuration: UseCase.Configuration, repository: ServicesRepository
    ): ServiceUseCases = ServiceUseCases(
        getServiceUseCase = GetServiceUseCase(configuration, repository),
        getServicesUseCase = GetServicesUseCase(configuration, repository),
        saveServiceUseCase = SaveServiceUseCase(configuration, repository),
        deleteServiceUseCase = DeleteServiceUseCase(configuration, repository)
    )
}