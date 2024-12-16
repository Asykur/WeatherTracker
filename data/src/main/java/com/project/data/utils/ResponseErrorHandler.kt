package com.project.data.utils

import com.google.gson.JsonParser
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException


fun getErrorResponse(error: Throwable): Pair<Int, String> {
    var message = "Server having trouble"
    var code = 0
    message = when (error) {
        is HttpException -> {
            code = error.code()
            try {
                val errorJsonString = error.response()?.errorBody()?.string()
                val jsonObject = JsonParser.parseString(errorJsonString).asJsonObject
                jsonObject.getAsJsonObject("error")?.get("message")?.asString ?: "Unknown error"
            } catch (e: Exception) {
                message
            }
        }
        is UnknownHostException -> {
            code = 503
            "No internet connection"
        }
        is SSLException -> {
            code = 495
            "SSL Error"
        }
        is SocketTimeoutException -> {
            code = 408
            "Request timed out"
        }
        else -> {
            code = 500
            error.message ?: message
        }
    }
    return Pair(code, message)
}