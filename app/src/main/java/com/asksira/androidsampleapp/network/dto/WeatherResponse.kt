package com.asksira.androidsampleapp.network.dto

import com.google.gson.annotations.SerializedName

data class WeatherResponse (
    @SerializedName("main")
    val weatherData: WeatherData,

    @SerializedName("name")
    val locationName: String
)