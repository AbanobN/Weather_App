package com.example.weatherapplication.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.databinding.HoursItemBinding
import com.example.weatherapplication.utiltes.convertTemperature
import com.example.weatherapplication.utiltes.formatDate
import com.example.weatherapplication.utiltes.getWeatherIconResource

class HourlyAdapter(private val hourlyForecasts: List<ForecastItem>, private val tempUnit: String) :
    RecyclerView.Adapter<HourlyAdapter.HourlyForecastViewHolder>() {

    class HourlyForecastViewHolder(private val binding: HoursItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(forecast: ForecastItem , unit: String) {
            // Format the hour
            val formattedHour = formatDate(forecast.dt,"ha")

            binding.txtHour.text = formattedHour
            convertTemperature(forecast.main.temp,unit).also { binding.txtHourDeg.text = it }

            val weatherIconCode = forecast.weather[0].icon
            val iconResource = getWeatherIconResource(weatherIconCode)
            binding.imgHour.setImageResource(iconResource)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastViewHolder {
        val binding = HoursItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyForecastViewHolder, position: Int) {
        holder.bind(hourlyForecasts[position], tempUnit)
    }

    override fun getItemCount() = hourlyForecasts.size

}
