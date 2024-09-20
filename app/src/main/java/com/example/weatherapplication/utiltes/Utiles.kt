package com.example.weatherapplication.utiltes

import com.example.weatherapplication.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun convertTemperature(temp: Double, scale: String = "K"): String {
    return when (scale) {
        "C" -> "${(temp - 273.15).toInt()}°C"
        "F" -> "${((temp - 273.15) * 9/5 + 32).toInt()}°F"
        else -> (temp.toInt()).toString()
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
        "01d" -> R.drawable.clear_sky      // Clear sky (day)
        "01n" -> R.drawable.clear_sky_night    // Clear sky (night)

        // Few clouds
        "02d" -> R.drawable.few_cloud     // Few clouds (day)
        "02n" -> R.drawable.few_cloud_night   // Few clouds (night)

        // Scattered clouds
        "03d" -> R.drawable.cloudy   // Scattered clouds (day)
        "03n" -> R.drawable.cloudy_night   // Scattered clouds (night)

        // Broken clouds
        "04d" -> R.drawable.broken_cloud      // Broken clouds (day)
        "04n" -> R.drawable.broken_cloud_night      // Broken clouds (night)

        // Shower rain
        "09d" -> R.drawable.rain    // Shower rain (day)
        "09n" -> R.drawable.rain_night  // Shower rain (night)

        // Rain
        "10d" -> R.drawable.rain       // Rain (day)
        "10n" -> R.drawable.rain_night     // Rain (night)

        // Thunderstorm
        "11d" -> R.drawable.thunderstorm   // Thunderstorm (day)
        "11n" -> R.drawable.thunderstorm_night // Thunderstorm (night)

        // Snow
        "13d" -> R.drawable.snow       // Snow (day)
        "13n" -> R.drawable.snow_night     // Snow (night)

        // Mist
        "50d" -> R.drawable.mist       // Mist (day)
        "50n" -> R.drawable.mist_night     // Mist (night)

        // Default
        else -> R.drawable.default_weather_icon
    }
}