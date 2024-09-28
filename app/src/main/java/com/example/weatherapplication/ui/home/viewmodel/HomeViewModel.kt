package com.example.weatherapplication.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.repository.IWeatherRepository
import com.example.weatherapplication.utiltes.ForecastApiState
import com.example.weatherapplication.utiltes.InternetState
import com.example.weatherapplication.utiltes.WeatherApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel (private val weatherRepository: IWeatherRepository,
                     private val internetState: InternetState) : ViewModel() {

    private val _tempUnit = MutableStateFlow<String>("")
    val tempUnit: StateFlow<String> get() = _tempUnit

    private val _windSpeed = MutableStateFlow<String>("")
    val windSpeed: StateFlow<String> get() = _windSpeed

    private val _isInternetAvailable = MutableStateFlow(false)
    val isInternetAvailable: StateFlow<Boolean> = _isInternetAvailable

    private val _weatherApiState = MutableStateFlow<WeatherApiState>(WeatherApiState.Loading)
    val weatherApiState: StateFlow<WeatherApiState> get() = _weatherApiState

    private val _forecastApiState = MutableStateFlow<ForecastApiState>(ForecastApiState.Loading)
    val forecastApiState: StateFlow<ForecastApiState> get() = _forecastApiState



    fun fetchWeatherData(lat: Double, lon: Double , isNetwork: Boolean) {
        viewModelScope.launch {
            _weatherApiState.value = WeatherApiState.Loading

            weatherRepository.fetchWeatherAndUpdate(lat=lat, lon=lon, isNetwork = isNetwork)
                .catch { e ->
                    _weatherApiState.value = WeatherApiState.Failure(e)
                }
                .collect { weatherResponse ->
                 _weatherApiState.value = WeatherApiState.Success(weatherResponse)
            }
        }
    }


    fun fetchForecastData(lat: Double, lon: Double, isNetwork: Boolean) {
        viewModelScope.launch {
            _forecastApiState.value = ForecastApiState.Loading
            weatherRepository.fetchForecastAndUpdate(lat=lat, lon=lon, isNetwork = isNetwork)
                .catch { e ->
                    _forecastApiState.value = ForecastApiState.Failure(e)
                }
                .collect { (dailyForecasts, hourlyForecasts) ->
                    _forecastApiState.value = ForecastApiState.Success(dailyForecasts,hourlyForecasts)
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