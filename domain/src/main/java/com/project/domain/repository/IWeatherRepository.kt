package com.project.domain.repository

import com.project.common.utils.Resource
import com.project.domain.data.WeatherDataDomain
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    fun searchWeather(query : String) : Flow<Resource<WeatherDataDomain>>
}