package com.oborodulin.home.data.di

import com.google.gson.Gson
import com.oborodulin.home.common.di.CommonModule
import com.oborodulin.home.common.domain.usecases.UseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CommonModule::class]
)
object TestCommonModule {
    @Singleton
    @Provides
    fun provideJsonLogger(): Gson = Gson()

    @Singleton
    @Provides
    fun provideUseCaseConfiguration(): UseCase.Configuration =
        UseCase.Configuration(StandardTestDispatcher()) // Dispatchers.Unconfined
}