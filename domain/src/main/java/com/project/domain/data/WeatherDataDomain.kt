package com.project.domain.data

data class WeatherDataDomain(
    val current: CurrentDomain ?= null,
    val location: LocationDomain ?= null
)

data class CurrentDomain(
    val condition: ConditionDomain?,
    val feelslike_c: Double?,
    val humidity: Int?,
    val temp_c: Double?,
    val uv: Double?,
)

data class LocationDomain(
    val name: String?,
)

data class ConditionDomain(
    val code: Int?,
    val icon: String?,
    val text: String?
)
