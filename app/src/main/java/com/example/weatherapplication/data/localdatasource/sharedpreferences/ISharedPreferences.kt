package com.example.weatherapplication.data.localdatasource.sharedpreferences

interface ISharedPreferences {
    fun saveLocation(language: String)
    fun getLocation(): String
    fun saveLanguage(language: String)
    fun getLanguage(): String
    fun saveSpeed(language: String)
    fun getSpeed(): String
    fun saveUnit(language: String)
    fun getUnit(): String
    fun saveNotification(language: String)
    fun getNotification(): String
    fun saveRequestCode(requestCode: Int)
    fun getRequestCode(): Int
}