package com.project.data.source.remote.network

import com.project.data.source.remote.response.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {

    @GET("v1/current.json")
    suspend fun searchWeather(
        @Query("q") query: String,
        @Query("key") apiKey: String
    ): WeatherResponse


}