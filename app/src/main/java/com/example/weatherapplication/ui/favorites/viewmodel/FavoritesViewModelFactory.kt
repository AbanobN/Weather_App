package com.example.weatherapplication.ui.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapplication.data.repository.WeatherRepository
import com.example.weatherapplication.utiltes.InternetState

class FavoritesViewModelFactory(private val weatherRepository: WeatherRepository ,private val internetState: InternetState) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(weatherRepository,internetState) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}