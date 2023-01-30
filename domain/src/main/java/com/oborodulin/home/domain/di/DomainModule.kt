package com.oborodulin.home.domain.di

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.domain.repositories.PayersRepository
import com.oborodulin.home.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Singleton
    @Provides
    fun providePayerUseCases(configuration: UseCase.Configuration, repository: PayersRepository):
            PayerUseCases =
        PayerUseCases(
            getPayerUseCase = GetPayerUseCase(configuration, repository),
            getPayersUseCase = GetPayersUseCase(configuration, repository),
            savePayerUseCase = SavePayerUseCase(configuration, repository),
            deletePayerUseCase = DeletePayerUseCase(configuration, repository),
            favoritePayerUseCase = FavoritePayerUseCase(configuration, repository)
        )
}