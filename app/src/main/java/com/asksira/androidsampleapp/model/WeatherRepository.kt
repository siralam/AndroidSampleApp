package com.asksira.androidsampleapp.model

import com.asksira.androidsampleapp.network.OpenWeatherService
import com.asksira.androidsampleapp.network.dto.WeatherResponse
import com.asksira.androidsampleapp.utils.di.ApiKey
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherService: OpenWeatherService,
    @ApiKey private val apiKey: String
) {

    suspend fun getWeatherByCityName(cityName: String): WeatherResponse {
        return weatherService.getWeatherByCityName(cityName, apiKey)
    }

}