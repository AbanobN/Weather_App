package com.example.weatherapplication.ui.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.pojo.City
import com.example.weatherapplication.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {

    private val _favoritesList = MutableStateFlow<List<City>>(emptyList())
    val favoritesList: StateFlow<List<City>> get() = _favoritesList

    fun fetchFavorites() {
        viewModelScope.launch {
            weatherRepository.getAllCities().collect { favorites ->
                _favoritesList.value = favorites
            }
        }
    }

    fun removeFavorite(cityName: String) {
        viewModelScope.launch {
            weatherRepository.deleteCity(cityName)
        }
    }
}