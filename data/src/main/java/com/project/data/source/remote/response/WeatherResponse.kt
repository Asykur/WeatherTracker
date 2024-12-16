package com.project.data.source.remote.response

data class WeatherResponse(
    val current: Current?,
    val location: Location?
)

data class Current(
    val condition: Condition?,
    val feelslike_c: Double?,
    val humidity: Int?,
    val temp_c: Double?,
    val uv: Double?,
)

data class Location(
    val name: String?,
)

data class Condition(
    val code: Int?,
    val icon: String?,
    val text: String?
)

