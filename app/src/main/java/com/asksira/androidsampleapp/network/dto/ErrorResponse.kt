package com.asksira.androidsampleapp.network.dto

import com.google.gson.annotations.SerializedName

data class ErrorResponse (

    @SerializedName("cod")
    val errorCode: String,

    @SerializedName("message")
    val errorMessage: String

)