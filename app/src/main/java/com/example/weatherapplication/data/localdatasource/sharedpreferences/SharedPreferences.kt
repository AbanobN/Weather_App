package com.example.weatherapplication.data.localdatasource.sharedpreferences

import android.content.Context

class SharedPreferences(context: Context) : ISharedPreferences {

    private val preferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    override fun saveLocation(language: String) {
        preferences.edit().putString("location", language).apply()
    }

    override fun getLocation(): String {
        return preferences.getString("location", "gps").toString()
    }

    override fun saveLanguage(language: String) {
        preferences.edit().putString("language", language).apply()
    }

    override fun getLanguage(): String {
        return preferences.getString("language", "en").toString()
    }

    override fun saveSpeed(language: String) {
        preferences.edit().putString("wind_speed_unit", language).apply()
    }

    override fun getSpeed(): String {
        return preferences.getString("wind_speed_unit", "mps").toString()
    }
    override fun saveUnit(language: String) {
        preferences.edit().putString("unit", language).apply()
    }

    override fun getUnit(): String {
        return preferences.getString("unit", "C").toString()
    }

    override fun saveNotification(language: String) {
        preferences.edit().putString("notification", language).apply()
    }

    override fun getNotification(): String {
        return preferences.getString("notification", "enable").toString()
    }
}