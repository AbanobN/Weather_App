package com.example.weatherapplication.ui.favorites.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.pojo.City
import com.example.weatherapplication.data.repository.WeatherRepository
import kotlinx.coroutines.launch

class FavoritesViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {

    private val _favoritesLiveData = MutableLiveData<List<City>>()
    val favoritesLiveData: LiveData<List<City>> get() = _favoritesLiveData

    fun fetchFavorites() {
        viewModelScope.launch {
            val favorites = weatherRepository.getAllCities()
            _favoritesLiveData.postValue(favorites)
        }
    }

    fun addFavorite(city: City) {
        viewModelScope.launch {
            weatherRepository.insertCity(city)
            fetchFavorites() // Refresh the list after adding
        }
    }

    fun removeFavorite(cityName: String) {
        viewModelScope.launch {
            weatherRepository.deleteCity(cityName)
            fetchFavorites() // Refresh the list after removing
        }
    }
}