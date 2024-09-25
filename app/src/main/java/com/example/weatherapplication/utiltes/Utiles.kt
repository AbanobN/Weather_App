package com.example.weatherapplication.utiltes

import android.content.Context
import com.example.weatherapplication.R
import com.example.weatherapplication.data.localdatasource.sharedpreferences.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


fun parseIntegerIntoArabic(number: String, context: Context): String {
    val sharedPreferencesHelper = SharedPreferences(context)

    if (sharedPreferencesHelper.getLanguage() == "ar") {
        val arabicDigitsMap = mapOf(
            '0' to '٠',
            '1' to '١',
            '2' to '٢',
            '3' to '٣',
            '4' to '٤',
            '5' to '٥',
            '6' to '٦',
            '7' to '٧',
            '8' to '٨',
            '9' to '٩',
        )

        return number.map { arabicDigitsMap[it] ?: it }.joinToString("")
    } else {
        return number
    }
}


fun convertTemperature(temp: Double, scale: String = "K"): String {
    return when (scale) {
        "C" -> "${((temp - 273.15).toInt())}°C"
        "F" -> "${((temp - 273.15) * 9/5 + 32).toInt()}°F"
        else -> "${(temp.toInt()).toString()} K"
    }
}

fun convertToLocalTime(dt: Long, timezoneOffset: Int): Pair<String, String> {
    val localTime = Date((dt + timezoneOffset) * 1000L) // Convert seconds to milliseconds
    val dayFormat = SimpleDateFormat("EEE, d MMM", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    return Pair(dayFormat.format(localTime), timeFormat.format(localTime))
}

fun formatDate(unixTime: Long, pattern: String): String {
    val date = Date(unixTime * 1000) // Convert seconds to milliseconds
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return dateFormat.format(date)
}

fun convertWindSpeed(speed: Double, unit: String = "mps", locale: Locale = Locale.getDefault()): String {
    return when (unit) {
        "mph" -> String.format(locale, "%.2f m/h", speed * 2.23694)
        else -> String.format(locale, "%.2f m/s", speed)
    }
}

fun getWeatherIconResource(iconCode: String): Int {
    return when (iconCode) {
        // Clear sky
        "01d" -> R.drawable.clear_sky
        "01n" -> R.drawable.clear_sky_night

        // Few clouds
        "02d" -> R.drawable.few_cloud
        "02n" -> R.drawable.few_cloud_night

        // Scattered clouds
        "03d" -> R.drawable.cloudy
        "03n" -> R.drawable.cloudy_night

        // Broken clouds
        "04d" -> R.drawable.broken_cloud
        "04n" -> R.drawable.broken_cloud_night

        // Shower rain
        "09d" -> R.drawable.rain
        "09n" -> R.drawable.rain_night

        // Rain
        "10d" -> R.drawable.rain
        "10n" -> R.drawable.rain_night

        // Thunderstorm
        "11d" -> R.drawable.thunderstorm
        "11n" -> R.drawable.thunderstorm_night

        // Snow
        "13d" -> R.drawable.snow
        "13n" -> R.drawable.snow_night

        // Mist
        "50d" -> R.drawable.mist
        "50n" -> R.drawable.mist_night

        // Default
        else -> R.drawable.default_weather_icon
    }
}