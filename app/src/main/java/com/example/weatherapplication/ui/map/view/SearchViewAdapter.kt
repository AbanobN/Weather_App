package com.example.weatherapplication.ui.map.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.ui.map.viewmodel.MapViewModel

class SearchViewAdapter(
    private var countries: ArrayList<String>,
    private val onItemClick: (String) -> Unit,
    private val mapViewModel: MapViewModel
) : RecyclerView.Adapter<SearchViewAdapter.ViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        val country = countries[position]
        holder.countryTextView.text = country

        holder.countryTextView.setOnClickListener{

        }
    }

    override fun getItemCount(): Int = countries.size

    fun setCountries(newCountries: ArrayList<String>) {
        countries = newCountries
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countryTextView: TextView = itemView.findViewById(android.R.id.text1)
    }
}
