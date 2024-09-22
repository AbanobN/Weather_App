package com.example.weatherapplication.ui.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.pojo.City
import com.example.weatherapplication.data.pojo.Coord
import com.example.weatherapplication.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MapViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {

    private val _operationStatus = MutableStateFlow("")
    val operationStatus: StateFlow<String> get() = _operationStatus

    fun fetchAndInsertCity(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            weatherRepository.getLocationByCoordinates(lat, lon, apiKey)
                .catch { e ->
                    _operationStatus.value = "Failure"
                }
                .collect { nameResponse ->
                    val city = City(
                        name = nameResponse.name,
                        coord = Coord(lat = lat, lon = lon)
                    )
                    weatherRepository.insertCity(city)
                    _operationStatus.value = "Done"
                }
        }
    }
}