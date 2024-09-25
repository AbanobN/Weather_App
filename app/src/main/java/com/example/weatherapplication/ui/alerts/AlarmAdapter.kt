package com.example.weatherapplication.ui.alerts

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.weatherapplication.data.pojo.AlarmData
import com.example.weatherapplication.databinding.AlarmItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AlarmDiff:DiffUtil.ItemCallback<AlarmData>(){
    override fun areItemsTheSame(oldItem: AlarmData, newItem: AlarmData): Boolean {
        return oldItem.requestCode==newItem.requestCode
    }

    override fun areContentsTheSame(oldItem: AlarmData, newItem: AlarmData): Boolean {
        return oldItem==newItem
    }
}

class AlarmAdapter :ListAdapter<AlarmData,AlarmAdapter.AlarmViewHolder>(AlarmDiff())
{

    lateinit var binding: AlarmItemBinding
    class AlarmViewHolder( var binding: AlarmItemBinding):ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding= AlarmItemBinding.inflate(inflater,parent,false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val currentAlarm =getItem(position)
        holder.binding.time.text=convertMilliSecondsToTime(currentAlarm.time,"hh mm")
    }

    fun convertMilliSecondsToTime(milliSeconds: Long, pattern: String): String
    {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())


        return dateFormat.format(milliSeconds)
    }
}