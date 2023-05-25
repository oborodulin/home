package com.oborodulin.home.domain.di

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.domain.repositories.PayersRepository
import com.oborodulin.home.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseConfigurationModule {
    // USE CASES
    // Configuration:
    @Singleton
    @Provides
    fun provideUseCaseConfiguration(): UseCase.Configuration = UseCase.Configuration(Dispatchers.IO)
}