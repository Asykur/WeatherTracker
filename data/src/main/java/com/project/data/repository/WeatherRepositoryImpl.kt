package com.project.data.repository

import com.project.data.source.remote.network.DirectCall
import com.project.common.utils.Resource
import com.project.data.source.remote.RemoteDataSource
import com.project.data.source.remote.network.ApiResponse
import com.project.data.source.remote.response.WeatherResponse
import com.project.data.utils.DataMapper
import com.project.domain.data.WeatherDataDomain
import com.project.domain.repository.IWeatherRepository
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl(private val remoteDataSource: RemoteDataSource): IWeatherRepository {
    override fun searchWeather(query: String): Flow<Resource<WeatherDataDomain>> {
        return object : DirectCall<WeatherDataDomain, WeatherResponse>(){
            override suspend fun callResult(response: WeatherResponse): WeatherDataDomain {
                return DataMapper.mapWeatherResponseToDomain(response)
            }

            override suspend fun createCall(): ApiResponse<WeatherResponse> {
                return remoteDataSource.searchWeather(query)
            }

        }.asFlow()
    }

}