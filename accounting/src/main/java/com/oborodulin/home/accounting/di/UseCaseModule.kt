package com.oborodulin.home.accounting.di

import com.oborodulin.home.accounting.domain.repositories.PayersRepository
import com.oborodulin.home.accounting.domain.usecases.*
import com.oborodulin.home.common.domain.usecases.UseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideUseCaseConfiguration(): UseCase.Configuration = UseCase.Configuration(Dispatchers.IO)

    @Provides
    fun DeletePayerUseCase(configuration: UseCase.Configuration, repository: PayersRepository):
            DeletePayerUseCase = DeletePayerUseCase(configuration, repository)

    @Provides
    fun GetPayersUseCase(configuration: UseCase.Configuration, repository: PayersRepository):
            GetPayersUseCase = GetPayersUseCase(configuration, repository)

    @Provides
    fun GetPayerUseCase(configuration: UseCase.Configuration, repository: PayersRepository):
            GetPayerUseCase = GetPayerUseCase(configuration, repository)

    @Provides
    fun SavePayerUseCase(configuration: UseCase.Configuration, repository: PayersRepository):
            SavePayerUseCase = SavePayerUseCase(configuration, repository)

    @Singleton
    @Provides
    fun providePayerUseCases(configuration: UseCase.Configuration, repository: PayersRepository):
            PayerUseCases =
        PayerUseCases(
            getPayerUseCase = GetPayerUseCase(configuration, repository),
            getPayersUseCase = GetPayersUseCase(configuration, repository),
            savePayerUseCase = SavePayerUseCase(configuration, repository),
            deletePayerUseCase = DeletePayerUseCase(configuration, repository)
        )
}