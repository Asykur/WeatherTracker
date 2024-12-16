package com.project.domain.repository

import com.project.common.utils.Resource
import com.project.domain.data.WeatherDataDomain
import com.project.domain.usecase.WeatherUseCase
import kotlinx.coroutines.flow.Flow

class WeatherInterActor(private val repository: IWeatherRepository): WeatherUseCase {

    override fun searchWeather(query: String): Flow<Resource<WeatherDataDomain>> {
        return repository.searchWeather(query)
    }

}