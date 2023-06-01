package com.oborodulin.home.billing.di

import com.oborodulin.home.billing.data.mappers.*
import com.oborodulin.home.billing.data.repositories.BillingRepositoryImpl
import com.oborodulin.home.billing.data.repositories.sources.local.LocalBillingDataSource
import com.oborodulin.home.billing.data.sources.local.LocalBillingDataSourceImpl
import com.oborodulin.home.billing.domain.repositories.BillingRepository
import com.oborodulin.home.billing.domain.usecases.BillingUseCases
import com.oborodulin.home.billing.domain.usecases.GetPayerServiceSubtotalsUseCase
import com.oborodulin.home.billing.ui.model.converters.ServiceSubtotalListConverter
import com.oborodulin.home.billing.ui.model.mappers.PayerServiceSubtotalListToServiceSubtotalListItemMapper
import com.oborodulin.home.billing.ui.model.mappers.PayerServiceSubtotalToServiceSubtotalListItemMapper
import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.data.local.db.dao.RateDao
import com.oborodulin.home.servicing.data.mappers.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {
    // DATA MAPPERS:
    @Singleton
    @Provides
    fun providePayerServiceSubtotalDebtViewToPayerServiceDebtMapper(): PayerServiceSubtotalDebtViewToPayerServiceDebtMapper =
        PayerServiceSubtotalDebtViewToPayerServiceDebtMapper()

    @Singleton
    @Provides
    fun providePayerServiceSubtotalDebtViewListToPayerServiceDebtListMapper(mapper: PayerServiceSubtotalDebtViewToPayerServiceDebtMapper): PayerServiceSubtotalDebtViewListToPayerServiceDebtListMapper =
        PayerServiceSubtotalDebtViewListToPayerServiceDebtListMapper(mapper = mapper)

    @Singleton
    @Provides
    fun providePayerTotalDebtViewToPayerDebtMapper(): PayerTotalDebtViewToPayerDebtMapper =
        PayerTotalDebtViewToPayerDebtMapper()

    @Singleton
    @Provides
    fun providePayerTotalDebtViewListToPayerDebtListMapper(mapper: PayerTotalDebtViewToPayerDebtMapper): PayerTotalDebtViewListToPayerDebtListMapper =
        PayerTotalDebtViewListToPayerDebtListMapper(mapper = mapper)

    @Singleton
    @Provides
    fun provideBillingMappers(
        payerServiceSubtotalDebtViewListToPayerServiceDebtListMapper: PayerServiceSubtotalDebtViewListToPayerServiceDebtListMapper,
        payerTotalDebtViewToPayerDebtMapper: PayerTotalDebtViewToPayerDebtMapper,
        payerTotalDebtViewListToPayerDebtListMapper: PayerTotalDebtViewListToPayerDebtListMapper
    ): BillingMappers = BillingMappers(
        payerServiceSubtotalDebtViewListToPayerServiceDebtListMapper,
        payerTotalDebtViewToPayerDebtMapper,
        payerTotalDebtViewListToPayerDebtListMapper
    )

    // UI MAPPERS:
    @Singleton
    @Provides
    fun provideServiceToServiceSubtotalListItemMapper(): PayerServiceSubtotalToServiceSubtotalListItemMapper =
        PayerServiceSubtotalToServiceSubtotalListItemMapper()

    @Singleton
    @Provides
    fun provideServiceListToServiceSubtotalListItemMapper(mapper: PayerServiceSubtotalToServiceSubtotalListItemMapper): PayerServiceSubtotalListToServiceSubtotalListItemMapper =
        PayerServiceSubtotalListToServiceSubtotalListItemMapper(
            mapper = mapper
        )

    // CONVERTERS:
    @Singleton
    @Provides
    fun provideServiceSubtotalListConverter(mapper: PayerServiceSubtotalListToServiceSubtotalListItemMapper): ServiceSubtotalListConverter =
        ServiceSubtotalListConverter(mapper = mapper)

    // DATA SOURCES:
    @Singleton
    @Provides
    fun provideBillingDataSourceImp(
        rateDao: RateDao, @IoDispatcher dispatcher: CoroutineDispatcher
    ): LocalBillingDataSource = LocalBillingDataSourceImpl(rateDao, dispatcher)

    // REPOSITORIES:
    @Singleton
    @Provides
    fun provideBillingRepository(
        localBillingDataSource: LocalBillingDataSource, mappers: BillingMappers
    ): BillingRepository = BillingRepositoryImpl(localBillingDataSource, mappers)

    // USE CASES:
    @Singleton
    @Provides
    fun provideBillingUseCases(
        configuration: UseCase.Configuration,
        repository: BillingRepository
    ): BillingUseCases = BillingUseCases(
        getPayerServiceSubtotalsUseCase = GetPayerServiceSubtotalsUseCase(
            configuration,
            repository
        )
    )
}