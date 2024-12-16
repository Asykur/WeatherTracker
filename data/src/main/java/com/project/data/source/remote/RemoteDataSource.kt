package com.project.data.source.remote

import com.project.data.source.remote.network.ApiResponse
import com.project.data.source.remote.network.ApiServices
import com.project.common.network.UrlInterface
import com.project.data.source.remote.response.WeatherResponse
import com.project.data.utils.getErrorResponse

class RemoteDataSource(
    private val apiServices: ApiServices,
    private val urlInterface: UrlInterface
) {

    suspend fun searchWeather(query: String): ApiResponse<WeatherResponse> {
        return try {
            val response = apiServices.searchWeather(query, urlInterface.apiKey)
            ApiResponse.Success(response)
        } catch (t: Throwable) {
            val (code, message) = getErrorResponse(t)
            ApiResponse.Error(code, message)
        }
    }

}