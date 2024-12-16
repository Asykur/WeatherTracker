package com.project.data.utils

import com.project.data.source.remote.response.WeatherResponse
import com.project.domain.data.ConditionDomain
import com.project.domain.data.CurrentDomain
import com.project.domain.data.LocationDomain
import com.project.domain.data.WeatherDataDomain

object DataMapper {
    fun mapWeatherResponseToDomain(response: WeatherResponse): WeatherDataDomain {
        val condition = ConditionDomain(
            code = response.current?.condition?.code,
            icon = response.current?.condition?.icon,
            text = response.current?.condition?.text
        )
        val currentDomain = CurrentDomain(
            condition = condition,
            feelslike_c = response.current?.feelslike_c,
            humidity = response.current?.humidity,
            temp_c = response.current?.temp_c,
            uv = response.current?.uv
        )
        val locationDomain = LocationDomain(name = response.location?.name)
        return WeatherDataDomain(current = currentDomain, location = locationDomain)
    }
}