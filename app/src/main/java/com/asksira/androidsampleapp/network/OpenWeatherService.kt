package com.asksira.androidsampleapp.network

import com.asksira.androidsampleapp.network.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherService {

    @GET("weather")
    suspend fun getWeatherByCityName(
        @Query("q") cityName: String,
        @Query("appid") appId: String,
        @Query("mode") mode: String? = null,
        @Query("units") units: String? = null,
        @Query("lang") language: String? = null
    ): WeatherResponse

}