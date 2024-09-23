package com.example.weatherapplication.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.WeatherResponse
import com.example.weatherapplication.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel (private val weatherRepository: WeatherRepository) : ViewModel() {

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
                    Log.d("TAG1", "fetchWeatherData: ${e.message}")
                }
                .collect { weatherResponse ->
                _weatherData.value = weatherResponse
            }
        }
    }


    fun fetchForecastData(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            weatherRepository.fetchForecast(lat, lon, apiKey)
                .catch { e ->
                    Log.d("TAG1", "fetchForecastData: ${e.message}")
                }
                .collect { (dailyForecasts, hourlyForecasts) ->
                    _days.value = dailyForecasts
                    _hours.value = hourlyForecasts
                }
        }
    }

}