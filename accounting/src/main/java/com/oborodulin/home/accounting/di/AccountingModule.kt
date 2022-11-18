package com.oborodulin.home.accounting.di

import com.oborodulin.home.accounting.domain.usecases.*
import com.oborodulin.home.accounting.ui.model.converters.PayerConverter
import com.oborodulin.home.accounting.ui.model.converters.PayersListConverter
import com.oborodulin.home.accounting.ui.model.converters.PrevServiceMeterValuesConverter
import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.domain.repositories.PayersRepository
import com.oborodulin.home.domain.usecase.*
import com.oborodulin.home.metering.domain.repositories.MetersRepository
import com.oborodulin.home.metering.domain.usecases.GetPrevServiceMeterValuesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AccountingModule {

    @Singleton
    @Provides
    fun providePayersListConverter(): PayersListConverter = PayersListConverter()

    @Singleton
    @Provides
    fun providePayerConverter(): PayerConverter = PayerConverter()

    @Singleton
    @Provides
    fun providePrevServiceMeterValuesConverter(): PrevServiceMeterValuesConverter =
        PrevServiceMeterValuesConverter()

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