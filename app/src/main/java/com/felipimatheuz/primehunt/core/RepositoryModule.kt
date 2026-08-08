package com.felipimatheuz.primehunt.core

import com.felipimatheuz.primehunt.data.repository.*
import com.felipimatheuz.primehunt.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPrimeRepository(
        primeRepositoryImpl: PrimeRepositoryImpl
    ): PrimeRepository

    @Binds
    @Singleton
    abstract fun bindOverviewRepository(
        overviewRepositoryImpl: OverviewRepositoryImpl
    ): OverviewRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(
        goalRepositoryImpl: GoalRepositoryImpl
    ): GoalRepository

    @Binds
    @Singleton
    abstract fun bindSyncRepository(
        syncRepositoryImpl: SyncRepositoryImpl
    ): SyncRepository

    @Binds
    @Singleton
    abstract fun bindCloudRepository(
        cloudRepositoryImpl: CloudRepositoryImpl
    ): CloudRepository

    @Binds
    @Singleton
    abstract fun bindUiPreferencesRepository(
        uiPreferencesRepositoryImpl: UiPreferencesRepositoryImpl
    ): UiPreferencesRepository
}
