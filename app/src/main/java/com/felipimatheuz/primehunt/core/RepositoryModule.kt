package com.felipimatheuz.primehunt.core

import com.felipimatheuz.primehunt.data.repository.GoalRepositoryImpl
import com.felipimatheuz.primehunt.data.repository.OverviewRepositoryImpl
import com.felipimatheuz.primehunt.data.repository.SyncRepositoryImpl
import com.felipimatheuz.primehunt.domain.repository.GoalRepository
import com.felipimatheuz.primehunt.domain.repository.OverviewRepository
import com.felipimatheuz.primehunt.domain.repository.SyncRepository
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
}
