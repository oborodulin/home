package com.oborodulin.home.accounting.di

import com.oborodulin.home.accounting.domain.usecases.*
import com.oborodulin.home.accounting.ui.model.converters.FavoritePayerConverter
import com.oborodulin.home.accounting.ui.model.converters.PayerConverter
import com.oborodulin.home.accounting.ui.model.converters.PayersListConverter
import com.oborodulin.home.accounting.ui.model.mappers.*
import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.domain.repositories.PayersRepository
import com.oborodulin.home.domain.usecases.*
import com.oborodulin.home.metering.domain.repositories.MetersRepository
import com.oborodulin.home.servicing.domain.repositories.ServicesRepository
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
    fun providePayerToPayerModelMapper(): PayerToPayerUiMapper = PayerToPayerUiMapper()

    @Singleton
    @Provides
    fun providePayerModelToPayerMapper(): PayerUiToPayerMapper = PayerUiToPayerMapper()

    @Singleton
    @Provides
    fun providePayerToPayerListItemModelMapper(): PayerToPayerListItemMapper =
        PayerToPayerListItemMapper()

    @Singleton
    @Provides
    fun providePayerListToPayerListItemModelMapper(mapper: PayerToPayerListItemMapper): PayerListToPayerListItemMapper =
        PayerListToPayerListItemMapper(mapper = mapper)

    // CONVERTERS:
    @Singleton
    @Provides
    fun providePayersListConverter(mapper: PayerListToPayerListItemMapper): PayersListConverter =
        PayersListConverter(mapper = mapper)

    @Singleton
    @Provides
    fun providePayerConverter(mapper: PayerToPayerUiMapper): PayerConverter =
        PayerConverter(mapper = mapper)

    @Singleton
    @Provides
    fun provideFavoritePayerConverter(mapper: PayerToPayerUiMapper): FavoritePayerConverter =
        FavoritePayerConverter(mapper = mapper)

    // USE CASES:
    @Singleton
    @Provides
    fun provideAccountingUseCases(
        configuration: UseCase.Configuration,
        payersRepository: PayersRepository,
        metersRepository: MetersRepository
    ): AccountingUseCases =
        AccountingUseCases(
            getFavoritePayerUseCase = GetFavoritePayerUseCase(
                configuration,
                payersRepository
            ),
        )

    @Singleton
    @Provides
    fun providePayerUseCases(
        configuration: UseCase.Configuration, payersRepository: PayersRepository,
        servicesRepository: ServicesRepository
    ): PayerUseCases =
        PayerUseCases(
            getPayerUseCase = GetPayerUseCase(configuration, payersRepository),
            getPayersUseCase = GetPayersUseCase(
                configuration, payersRepository, servicesRepository
            ),
            savePayerUseCase = SavePayerUseCase(configuration, payersRepository),
            deletePayerUseCase = DeletePayerUseCase(configuration, payersRepository),
            favoritePayerUseCase = FavoritePayerUseCase(configuration, payersRepository)
        )

}