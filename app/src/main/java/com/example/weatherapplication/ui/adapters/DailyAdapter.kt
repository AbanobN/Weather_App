package com.example.weatherapplication.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.R
import com.example.weatherapplication.data.pojo.ForecastItem
import com.example.weatherapplication.databinding.DaysItemBinding
import com.example.weatherapplication.utiltes.convertTemperature
import com.example.weatherapplication.utiltes.formatDate
import com.example.weatherapplication.utiltes.getWeatherIconResource
import com.example.weatherapplication.utiltes.parseIntegerIntoArabic

class DailyAdapter(private val dailyForecasts: List<ForecastItem>, private val tempUnit: String) :
    RecyclerView.Adapter<DailyAdapter.DailyForecastViewHolder>() {

    class DailyForecastViewHolder(private val binding: DaysItemBinding, private var context: Context) : RecyclerView.ViewHolder(binding.root) {

        fun bind(forecast: ForecastItem, isTomorrow: Boolean = false , unit: String) {
            val formattedDay = formatDate(forecast.dt,"EEEE")

            binding.txtDay.text = if (isTomorrow) context.getString(R.string.tomorrow) else formattedDay
            binding.txtWeatherDes.text = forecast.weather[0].description

            // Set temperature range
            val tempMin = convertTemperature(forecast.main.temp_min,unit)
            val tempMax = convertTemperature(forecast.main.temp_max , unit)

            "${parseIntegerIntoArabic(tempMax, context)} / ${parseIntegerIntoArabic(tempMin, context)}".also { binding.txtWeather.text = it }

            val weatherIconCode = forecast.weather[0].icon
            val iconResource = getWeatherIconResource(weatherIconCode)
            binding.imgWeather.setImageResource(iconResource)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecastViewHolder {
        val binding = DaysItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyForecastViewHolder(binding ,parent.context)
    }

    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        val isTomorrow = position == 0
        holder.bind(dailyForecasts[position], isTomorrow, tempUnit)
    }

    override fun getItemCount() = dailyForecasts.size

}
