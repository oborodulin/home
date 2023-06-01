package com.oborodulin.home.servicing.di

import com.oborodulin.home.common.di.IoDispatcher
import com.oborodulin.home.data.local.db.dao.RateDao
import com.oborodulin.home.servicing.data.mappers.*
import com.oborodulin.home.servicing.data.repositories.RatesRepositoryImpl
import com.oborodulin.home.servicing.data.repositories.sources.local.LocalRateDataSource
import com.oborodulin.home.servicing.data.sources.local.LocalRateDataSourceImpl
import com.oborodulin.home.servicing.domain.repositories.RatesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RateModule {
    // DATA MAPPERS:
    @Singleton
    @Provides
    fun provideRateEntityToRateMapper(): RateEntityToRateMapper = RateEntityToRateMapper()

    @Singleton
    @Provides
    fun provideRateEntityListToRateListMapper(mapper: RateEntityToRateMapper): RateEntityListToRateListMapper =
        RateEntityListToRateListMapper(mapper = mapper)

    @Singleton
    @Provides
    fun provideRateToRateEntityMapper(): RateToRateEntityMapper = RateToRateEntityMapper()

    @Singleton
    @Provides
    fun provideRateMappers(
        rateEntityListToRateListMapper: RateEntityListToRateListMapper,
        rateEntityToRateMapper: RateEntityToRateMapper,
        rateToRateEntityMapper: RateToRateEntityMapper
    ): RateMappers = RateMappers(
        rateEntityListToRateListMapper,
        rateEntityToRateMapper,
        rateToRateEntityMapper
    )

    // UI MAPPERS:

    // CONVERTERS:

    // DATA SOURCES:
    @Singleton
    @Provides
    fun provideLocalRateDataSource(
        rateDao: RateDao, @IoDispatcher dispatcher: CoroutineDispatcher
    ): LocalRateDataSource = LocalRateDataSourceImpl(
        rateDao, dispatcher
    )

    // REPOSITORIES:
    @Singleton
    @Provides
    fun provideRatesRepository(
        localRateDataSource: LocalRateDataSource, rateMappers: RateMappers
    ): RatesRepository = RatesRepositoryImpl(
        localRateDataSource, rateMappers
    )

    // USE CASES:
}