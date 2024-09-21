package com.example.weatherapplication.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.data.pojo.WeatherResponse
import com.example.weatherapplication.data.repository.WeatherRepository
import kotlinx.coroutines.launch

class HomeViewModel (private val weatherRepository: WeatherRepository) : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> = _weatherData

    private val _days = MutableLiveData<List<ForecastItem>>()
    val days: LiveData<List<ForecastItem>> = _days

    private val _hours = MutableLiveData<List<ForecastItem>>()
    val hours: LiveData<List<ForecastItem>> = _hours

    fun fetchWeather(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                val combinedResponse = weatherRepository.fetchWeather(lat, lon, apiKey)
                _weatherData.postValue(combinedResponse!!) // Provide default values if null
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

    fun fetchForecast(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                val result = weatherRepository.fetchForecast(lat, lon, apiKey)
                result?.let { (dailyForecasts, hourlyForecasts) ->
                    _days.postValue(dailyForecasts)
                    _hours.postValue(hourlyForecasts)
                }
            } catch (e: Exception) {
                // Handle the exception
            }
        }
    }

}