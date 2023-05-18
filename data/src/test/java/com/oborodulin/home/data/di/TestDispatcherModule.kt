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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DispatcherModule::class]
)
object TestDispatcherModule {
    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher =
        StandardTestDispatcher() //Dispatchers.Unconfined

    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = StandardTestDispatcher()

    @MainDispatcher
    @Provides
    fun providesMainDispatcher(): CoroutineDispatcher = StandardTestDispatcher()
}
