package com.project.domain.usecase

import com.project.common.utils.Resource
import com.project.domain.data.WeatherDataDomain
import kotlinx.coroutines.flow.Flow

interface WeatherUseCase {
    fun searchWeather(query : String) : Flow<Resource<WeatherDataDomain>>
}