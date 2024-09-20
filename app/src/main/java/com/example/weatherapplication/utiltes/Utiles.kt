package com.example.weather_app_demo.utiltes

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

fun convertWindSpeed(speed: Double, unit: String = "mps"): String {
    return when (unit) {
        "mph" -> String.format("%.2f m/h", speed * 2.23694)
        else -> String.format("%.2f m/s", speed)
    }
}