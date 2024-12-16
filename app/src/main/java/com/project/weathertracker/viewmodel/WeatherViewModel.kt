package com.project.weathertracker.viewmodel

import androidx.compose.runtime.mutableStateOf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.common.utils.Resource
import com.project.domain.usecase.WeatherUseCase
import com.project.weathertracker.uistate.WeatherUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class WeatherViewModel(private val useCase: WeatherUseCase) : ViewModel() {
    private val _weatherUiSate = MutableSharedFlow<WeatherUiState>()
    val weatherUiSate get() = _weatherUiSate



    fun searchWeather(query: String) {
        viewModelScope.launch {
            useCase.searchWeather(query).collect { resource ->
                val state = when (resource) {
                    is Resource.Loading -> {
                        WeatherUiState.ShowLoading
                    }

                    is Resource.Success -> {
                        WeatherUiState.OnSuccess(resource.data)
                    }

                    is Resource.Error -> {
                        WeatherUiState.OnError(resource.errorMessage, resource.errorCode)
                    }
                }
                _weatherUiSate.emit(state)
            }
        }
    }


}