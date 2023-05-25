package com.oborodulin.home.data.di

import com.oborodulin.home.common.domain.usecases.UseCase
import com.oborodulin.home.domain.di.UseCaseConfigurationModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [UseCaseConfigurationModule::class]
)
object TestUseCaseConfigurationModule {
    // USE CASES
    // Configuration:
    @Singleton
    @Provides
    fun provideUseCaseConfiguration(scheduler: TestCoroutineScheduler): UseCase.Configuration =
        UseCase.Configuration(StandardTestDispatcher(scheduler))
}