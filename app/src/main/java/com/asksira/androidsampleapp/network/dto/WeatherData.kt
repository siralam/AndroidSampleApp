package com.asksira.androidsampleapp.network.dto

import com.google.gson.annotations.SerializedName

data class WeatherData (
    @SerializedName("temp")
    val temperature: Number?,

    @SerializedName("feels_like")
    val feelsLikeTemperature: Number?,

    @SerializedName("temp_min")
    val minTemperature: Number?,

    @SerializedName("temp_max")
    val maxTemperature: Number?,

    @SerializedName("pressure")
    val pressure: Number?,

    @SerializedName("humidity")
    val humidity: Int?,
)