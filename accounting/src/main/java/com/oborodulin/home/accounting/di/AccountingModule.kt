package com.oborodulin.home.accounting.di

import com.oborodulin.home.accounting.domain.usecases.*
import com.oborodulin.home.accounting.ui.model.converters.PayerConverter
import com.oborodulin.home.accounting.ui.model.converters.PayersListConverter
import com.oborodulin.home.metering.ui.model.converters.PrevServiceMeterValuesListConverter
import com.oborodulin.home.accounting.ui.model.mappers.*
import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.domain.repositories.PayersRepository
import com.oborodulin.home.domain.usecase.*
import com.oborodulin.home.metering.domain.repositories.MetersRepository
import com.oborodulin.home.metering.domain.usecases.GetPrevServiceMeterValuesUseCase
import com.oborodulin.home.metering.ui.model.mappers.PrevMetersValuesViewToMeterValueModelMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AccountingModule {
    // MAPPERS:
    @Singleton
    @Provides
    fun providePayerToPayerModelMapper(): PayerToPayerModelMapper = PayerToPayerModelMapper()

    @Singleton
    @Provides
    fun providePayerModelToPayerMapper(): PayerModelToPayerMapper= PayerModelToPayerMapper()

    @Singleton
    @Provides
    fun providePayerToPayerListItemModelMapper(): PayerToPayerListItemModelMapper =
        PayerToPayerListItemModelMapper()

    @Singleton
    @Provides
    fun providePayerListToPayerListItemModelMapper(mapper: PayerToPayerListItemModelMapper): PayerListToPayerListItemModelMapper =
        PayerListToPayerListItemModelMapper(mapper = mapper)

    // CONVERTERS:
    @Singleton
    @Provides
    fun providePayersListConverter(mapper: PayerListToPayerListItemModelMapper): PayersListConverter =
        PayersListConverter(mapper = mapper)

    @Singleton
    @Provides
    fun providePayerConverter(mapper: PayerToPayerModelMapper): PayerConverter =
        PayerConverter(mapper = mapper)

    // USE CASES:
    @Singleton
    @Provides
    fun provideAccountingUseCases(
        configuration: UseCase.Configuration,
        payersRepository: PayersRepository,
        metersRepository: MetersRepository
    ): AccountingUseCases =
        AccountingUseCases(
            getPrevServiceMeterValuesUseCase = GetPrevServiceMeterValuesUseCase(
                configuration,
                metersRepository
            ),
        )
}