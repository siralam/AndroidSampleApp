package com.asksira.androidsampleapp.network

import com.asksira.androidsampleapp.utils.di.ApiKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherAPIModule {

    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    private fun createRetrofit(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherService(): OpenWeatherService {
        return createRetrofit().create(OpenWeatherService::class.java)
    }

    @Provides
    @Singleton
    @ApiKey
    fun providesWeatherApiKey(): String {
        return ApiKeyStore.getWeatherApiKey()
    }

}