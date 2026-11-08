package com.felipimatheuz.primehunt.core

import com.felipimatheuz.primehunt.data.remote.firebase.FirebaseSquadRemoteDataSource
import com.felipimatheuz.primehunt.data.remote.firebase.SquadRemoteDataSource
import com.felipimatheuz.primehunt.data.repository.SquadRepositoryImpl
import com.felipimatheuz.primehunt.domain.repository.SquadRepository
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SquadModule {

    @Binds
    @Singleton
    abstract fun bindSquadRemoteDataSource(
        impl: FirebaseSquadRemoteDataSource
    ): SquadRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindSquadRepository(
        impl: SquadRepositoryImpl
    ): SquadRepository

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseDatabase(): FirebaseDatabase {
            return Firebase.database
        }
    }
}
