package com.oborodulin.home.data.di

import com.oborodulin.home.common.di.DefaultDispatcher
import com.oborodulin.home.common.di.DispatcherModule
import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.common.di.MainDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DispatcherModule::class]
)
object TestDispatcherModule {
    // Scheduler:
    @Singleton
    @Provides
    fun provideCoroutineScheduler(): TestCoroutineScheduler = TestCoroutineScheduler()

    @DefaultDispatcher
    @Provides
    fun provideDefaultDispatcher(scheduler: TestCoroutineScheduler): CoroutineDispatcher =
        StandardTestDispatcher(scheduler) //Dispatchers.Unconfined

    @IoDispatcher
    @Provides
    fun providesIoDispatcher(scheduler: TestCoroutineScheduler): CoroutineDispatcher =
        StandardTestDispatcher(scheduler)

    @MainDispatcher
    @Provides
    fun providesMainDispatcher(scheduler: TestCoroutineScheduler): CoroutineDispatcher =
        StandardTestDispatcher(scheduler)
}