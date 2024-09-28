package com.example.weatherapplication.ui.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.pojo.City
import com.example.weatherapplication.data.repository.IWeatherRepository
import com.example.weatherapplication.utiltes.InternetState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(private val weatherRepository: IWeatherRepository,
                         private val internetState: InternetState) : ViewModel() {

    private val _favoritesList = MutableStateFlow<List<City>>(emptyList())
    val favoritesList: StateFlow<List<City>> get() = _favoritesList

    private val _isInternetAvailable = MutableStateFlow(false)
    val isInternetAvailable: StateFlow<Boolean> = _isInternetAvailable


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

    fun observeNetwork() {
        viewModelScope.launch {
            val isAvailable = internetState.isInternetAvailable()
            _isInternetAvailable.value = isAvailable
        }
    }
}