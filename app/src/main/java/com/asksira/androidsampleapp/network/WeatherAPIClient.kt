package com.asksira.androidsampleapp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherAPIClient {

    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val weatherService: OpenWeatherService = retrofit.create(OpenWeatherService::class.java)

    val weatherApiKey by lazy {
        ApiKeyStore.getWeatherApiKey()
    }



}