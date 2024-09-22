package com.example.weatherapplication.ui.favorites.view

import com.example.weatherapplication.data.pojo.City

interface FavoritesClickListener {
    fun onCancelClick(city: City)
    fun onItemClick(city: City)
}