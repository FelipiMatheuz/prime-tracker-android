package com.felipimatheuz.primehunt.core

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.felipimatheuz.primehunt.service.api.PrimeTrackerService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val mapper =
            ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/FelipiMatheuz/WPH/main/data/")
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .build()
    }

    @Provides
    @Singleton
    fun provideTrackerService(retrofit: Retrofit): PrimeTrackerService {
        return retrofit.create(PrimeTrackerService::class.java)
    }
}
