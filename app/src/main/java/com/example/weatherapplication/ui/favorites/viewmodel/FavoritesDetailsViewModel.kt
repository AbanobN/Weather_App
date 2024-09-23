package com.example.weatherapplication.ui.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.WeatherResponse
import com.example.weatherapplication.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoritesDetailsViewModel (private val weatherRepository: WeatherRepository) : ViewModel() {

    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData

    private val _days = MutableStateFlow<List<ForecastItem>>(emptyList())
    val days: StateFlow<List<ForecastItem>> = _days

    private val _hours = MutableStateFlow<List<ForecastItem>>(emptyList())
    val hours: StateFlow<List<ForecastItem>> = _hours

    fun fetchWeatherData(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            weatherRepository.fetchWeather(lat, lon, apiKey)
                .catch { e ->
                    // Handle exception
                }
                .collect { response ->
                    _weatherData.value = response
                }
        }
    }

    fun fetchForecastData(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            weatherRepository.fetchForecast(lat, lon, apiKey)
                .catch { e ->
                    // Handle the exception
                }
                .collect { (dailyForecasts, hourlyForecasts) ->
                    _days.value = dailyForecasts
                    _hours.value = hourlyForecasts
                }
        }
    }

}