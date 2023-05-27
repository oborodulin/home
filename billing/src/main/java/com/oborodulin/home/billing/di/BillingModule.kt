package com.oborodulin.home.billing.di

import com.oborodulin.home.billing.data.mappers.*
import com.oborodulin.home.billing.data.repositories.BillingDataSource
import com.oborodulin.home.billing.data.repositories.BillingDataSourceImpl
import com.oborodulin.home.billing.data.repositories.RatesRepositoryImpl
import com.oborodulin.home.billing.domain.repositories.RatesRepository
import com.oborodulin.home.billing.domain.usecases.BillingUseCases
import com.oborodulin.home.billing.domain.usecases.GetPayerServiceSubtotalsUseCase
import com.oborodulin.home.billing.ui.model.converters.ServiceSubtotalListConverter
import com.oborodulin.home.billing.ui.model.mappers.ServiceListToServiceSubtotalListItemMapper
import com.oborodulin.home.billing.ui.model.mappers.ServiceToServiceSubtotalListItemMapper
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
    fun provideRateEntityToRateMapper(): RateEntityToRateMapper =
        RateEntityToRateMapper()

    @Singleton
    @Provides
    fun provideRateEntityListToRateListMapper(mapper: RateEntityToRateMapper): RateEntityListToRateListMapper =
        RateEntityListToRateListMapper(mapper = mapper)

    @Singleton
    @Provides
    fun provideRateToRateEntityMapper(): RateToRateEntityMapper =
        RateToRateEntityMapper()

    @Singleton
    @Provides
    fun providePayerServiceSubtotalDebtViewToServiceMapper(): PayerServiceSubtotalDebtViewToServiceMapper =
        PayerServiceSubtotalDebtViewToServiceMapper()

    @Singleton
    @Provides
    fun providePayerServiceSubtotalDebtViewListToServiceListMapper(mapper: PayerServiceSubtotalDebtViewToServiceMapper): PayerServiceSubtotalDebtViewListToServiceListMapper =
        PayerServiceSubtotalDebtViewListToServiceListMapper(mapper = mapper)

    @Singleton
    @Provides
    fun providePayerTotalDebtViewToPayerMapper(): PayerTotalDebtViewToPayerMapper =
        PayerTotalDebtViewToPayerMapper()

    @Singleton
    @Provides
    fun providePayerTotalDebtViewListToPayerListMapper(mapper: PayerTotalDebtViewToPayerMapper): PayerTotalDebtViewListToPayerListMapper =
        PayerTotalDebtViewListToPayerListMapper(mapper = mapper)

    // UI MAPPERS:
    @Singleton
    @Provides
    fun provideServiceToServiceSubtotalListItemMapper(): ServiceToServiceSubtotalListItemMapper =
        ServiceToServiceSubtotalListItemMapper()

    @Singleton
    @Provides
    fun provideServiceListToServiceSubtotalListItemMapper(mapper: ServiceToServiceSubtotalListItemMapper): ServiceListToServiceSubtotalListItemMapper =
        ServiceListToServiceSubtotalListItemMapper(mapper = mapper)

    // CONVERTERS:
    @Singleton
    @Provides
    fun provideServiceSubtotalListConverter(mapper: ServiceListToServiceSubtotalListItemMapper): ServiceSubtotalListConverter =
        ServiceSubtotalListConverter(mapper = mapper)

    // DATA SOURCES:
    @Singleton
    @Provides
    fun provideBillingDataSourceImp(
        rateDao: RateDao,
        @IoDispatcher dispatcher: CoroutineDispatcher,
        rateEntityListToRateListMapper: RateEntityListToRateListMapper,
        rateEntityToRateMapper: RateEntityToRateMapper,
        payerServiceSubtotalDebtViewListToServiceListMapper: PayerServiceSubtotalDebtViewListToServiceListMapper,
        payerTotalDebtViewToPayerMapper: PayerTotalDebtViewToPayerMapper,
        payerTotalDebtViewListToPayerListMapper: PayerTotalDebtViewListToPayerListMapper,
        rateToRateEntityMapper: RateToRateEntityMapper
    ): BillingDataSource =
        BillingDataSourceImpl(
            rateDao,
            dispatcher,
            rateEntityListToRateListMapper,
            rateEntityToRateMapper,
            payerServiceSubtotalDebtViewListToServiceListMapper,
            payerTotalDebtViewToPayerMapper,
            payerTotalDebtViewListToPayerListMapper,
            rateToRateEntityMapper
        )

    // REPOSITORIES:
    @Singleton
    @Provides
    fun provideRatesRepository(billingDataSource: BillingDataSource): RatesRepository =
        RatesRepositoryImpl(billingDataSource)

    // USE CASES:
    @Singleton
    @Provides
    fun provideBillingUseCases(
        configuration: UseCase.Configuration, repository: RatesRepository
    ): BillingUseCases =
        BillingUseCases(
            getPayerServiceSubtotalsUseCase = GetPayerServiceSubtotalsUseCase(
                configuration,
                repository
            )
        )
}