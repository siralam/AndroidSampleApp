package com.asksira.androidsampleapp.utils

import com.asksira.androidsampleapp.network.dto.ErrorResponse
import com.google.gson.Gson
import retrofit2.HttpException

fun HttpException.getErrorObject(): ErrorResponse? {
    try {
        val errorBody = response()?.errorBody()?.string() ?: return null
        return Gson().fromJson(errorBody, ErrorResponse::class.java)
    } catch (e: Exception) {
        return null
    }
}