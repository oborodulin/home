package com.oborodulin.home.domain.di

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.domain.repositories.PayersRepository
import com.oborodulin.home.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCasesModule {
    // USE CASES
    // Payer:
    @Singleton
    @Provides
    fun provideGetPayerUseCase(
        configuration: UseCase.Configuration,
        payersRepository: PayersRepository
    ): GetPayerUseCase = GetPayerUseCase(configuration, payersRepository)

    @Singleton
    @Provides
    fun provideFavoritePayerUseCase(
        configuration: UseCase.Configuration,
        payersRepository: PayersRepository
    ): FavoritePayerUseCase = FavoritePayerUseCase(configuration, payersRepository)

    @Singleton
    @Provides
    fun provideGetFavoritePayerUseCase(
        configuration: UseCase.Configuration,
        payersRepository: PayersRepository
    ): GetFavoritePayerUseCase = GetFavoritePayerUseCase(configuration, payersRepository)

    @Singleton
    @Provides
    fun provideDeletePayerUseCase(
        configuration: UseCase.Configuration,
        payersRepository: PayersRepository
    ): DeletePayerUseCase = DeletePayerUseCase(configuration, payersRepository)

    @Singleton
    @Provides
    fun provideSavePayerUseCase(
        configuration: UseCase.Configuration,
        payersRepository: PayersRepository
    ): SavePayerUseCase = SavePayerUseCase(configuration, payersRepository)
}