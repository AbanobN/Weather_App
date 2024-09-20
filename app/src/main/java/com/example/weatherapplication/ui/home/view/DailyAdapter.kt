package com.example.weatherapplication.ui.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.databinding.DaysItemBinding
import com.example.weatherapplication.utiltes.convertTemperature
import com.example.weatherapplication.utiltes.formatDate
import com.example.weatherapplication.utiltes.getWeatherIconResource

class DailyAdapter(private val dailyForecasts: List<ForecastItem>, private val tempUnit: String) :
    RecyclerView.Adapter<DailyAdapter.DailyForecastViewHolder>() {

    class DailyForecastViewHolder(private val binding: DaysItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(forecast: ForecastItem, isTomorrow: Boolean = false , unit: String) {
            // Format the day
            val formattedDay = formatDate(forecast.dt,"EEEE")

            binding.txtDay.text = if (isTomorrow) "Tomorrow" else formattedDay
            binding.txtWeatherDes.text = forecast.weather[0].description

            // Set temperature range
            val tempMin = convertTemperature(forecast.main.temp_min,unit)
            val tempMax = convertTemperature(forecast.main.temp_max , unit)

            "$tempMax / $tempMin".also { binding.txtWeather.text = it }

            val weatherIconCode = forecast.weather[0].icon
            val iconResource = getWeatherIconResource(weatherIconCode)
            binding.imgWeather.setImageResource(iconResource)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecastViewHolder {
        val binding = DaysItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        val isTomorrow = position == 0
        holder.bind(dailyForecasts[position], isTomorrow, tempUnit)
    }

    override fun getItemCount() = dailyForecasts.size

}
