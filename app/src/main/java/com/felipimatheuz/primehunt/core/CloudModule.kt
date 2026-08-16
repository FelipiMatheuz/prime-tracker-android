package com.felipimatheuz.primehunt.core

import com.felipimatheuz.primehunt.data.cloud.Firestore
import com.felipimatheuz.primehunt.domain.repository.CloudRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CloudModule {

    @Binds
    @Singleton
    abstract fun bindCloudRemoteDataSource(
        firestore: Firestore
    ): CloudRemoteDataSource
}
