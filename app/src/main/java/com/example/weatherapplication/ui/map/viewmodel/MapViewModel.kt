package com.example.weatherapplication.ui.map.viewmodel

import androidx.lifecycle.ViewModel
import com.example.weatherapplication.data.pojo.City
import com.example.weatherapplication.data.pojo.Coord
import com.example.weatherapplication.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapViewModel (private val weatherRepository: WeatherRepository) : ViewModel() {

    private val _operationStatus = MutableStateFlow<String>("")
    val operationStatus: StateFlow<String> get() = _operationStatus

    suspend fun fetchAndInsertCity(lat: Double, lon: Double, apiKey: String) {

        val locationResponse = weatherRepository.getLocationByCoordinates(lat, lon, apiKey)
        locationResponse?.let { response ->
            val city = City(
                name = response.name,
                coord = Coord(lat = lat, lon = lon),
            )
            weatherRepository.insertCity(city)
            _operationStatus.value = "Done"
        }?: run {
            _operationStatus.value = "Failure"
        }
    }
}