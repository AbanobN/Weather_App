package com.example.weatherapplication.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.WeatherResponse
import com.example.weatherapplication.data.repository.IWeatherRepository
import com.example.weatherapplication.utiltes.InternetState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel (private val weatherRepository: IWeatherRepository,
                     private val internetState: InternetState) : ViewModel() {

    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData

    private val _days = MutableStateFlow<List<ForecastItem>>(emptyList())
    val days: StateFlow<List<ForecastItem>> = _days

    private val _hours = MutableStateFlow<List<ForecastItem>>(emptyList())
    val hours: StateFlow<List<ForecastItem>> = _hours

    private val _tempUnit = MutableStateFlow<String>("")
    val tempUnit: StateFlow<String> get() = _tempUnit

    private val _windSpeed = MutableStateFlow<String>("")
    val windSpeed: StateFlow<String> get() = _windSpeed

    private val _isInternetAvailable = MutableStateFlow(false)
    val isInternetAvailable: StateFlow<Boolean> = _isInternetAvailable


    init {
        observeNetwork()
    }


    fun fetchWeatherData(lat: Double, lon: Double , isNetwork: Boolean) {
        viewModelScope.launch {

            weatherRepository.fetchWeatherAndUpdate(lat=lat, lon=lon, isNetwork = isNetwork)
                .catch { e ->
                    Log.d("TAG1", "fetchWeatherData: ${e.message}")
                }
                .collect { weatherResponse ->
                _weatherData.value = weatherResponse
            }
        }
    }


    fun fetchForecastData(lat: Double, lon: Double, isNetwork: Boolean) {
        viewModelScope.launch {

            weatherRepository.fetchForecastAndUpdate(lat=lat, lon=lon, isNetwork = isNetwork)
                .catch { e ->
                    Log.d("TAG1", "fetchForecastData: ${e.message}")
                }
                .collect { (dailyForecasts, hourlyForecasts) ->
                    _days.value = dailyForecasts
                    _hours.value = hourlyForecasts
                }
        }
    }

    fun updateSettings() {
        _tempUnit.value = weatherRepository.getUnit()
        _windSpeed.value = weatherRepository.getSpeed()
    }

    fun observeNetwork() {
        viewModelScope.launch {
            val isAvailable = internetState.isInternetAvailable()
            _isInternetAvailable.value = isAvailable
        }
    }

}