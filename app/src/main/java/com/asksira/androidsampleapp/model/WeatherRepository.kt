package com.asksira.androidsampleapp.model

import com.asksira.androidsampleapp.network.WeatherAPIClient
import com.asksira.androidsampleapp.network.dto.WeatherResponse

class WeatherRepository {

    suspend fun getWeatherByCityName(cityName: String): WeatherResponse {
        return WeatherAPIClient.weatherService.getWeatherByCityName(cityName,
            WeatherAPIClient.weatherApiKey
        )
    }

}