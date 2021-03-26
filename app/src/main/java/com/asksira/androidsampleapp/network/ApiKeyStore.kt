package com.asksira.androidsampleapp.network

object ApiKeyStore {

    init {
        System.loadLibrary("native-lib")
    }

    external fun getWeatherApiKey(): String

}