package com.example.weatherapplication.ui.favorites.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.R
import com.example.weatherapplication.data.pojo.City
import com.example.weatherapplication.databinding.FavoritesItemBinding


class CityDiffUtil : DiffUtil.ItemCallback<City>() {
    override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem == newItem
    }
}

class FavoritesAdapter(private val listener: FavoritesClickListener) : ListAdapter<City, FavoritesAdapter.ViewHolder>(CityDiffUtil()) {


    class ViewHolder(private val binding: FavoritesItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(city: City, listener: FavoritesClickListener) {
            binding.txtCityName.text = city.name

            binding.cancelButton.setOnClickListener {
                listener.onCancelClick(city)
            }

            binding.root.setOnClickListener {
                listener.onItemClick(city)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FavoritesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = getItem(position)
        holder.bind(city, listener)
    }

}