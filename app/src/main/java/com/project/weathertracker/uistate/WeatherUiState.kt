package com.project.weathertracker.uistate

import com.project.domain.data.WeatherDataDomain

sealed class WeatherUiState {
    object ShowLoading : WeatherUiState()
    data class OnSuccess(val data : WeatherDataDomain?): WeatherUiState()
    data class OnError(val message : String?, val code : Int?): WeatherUiState()
}