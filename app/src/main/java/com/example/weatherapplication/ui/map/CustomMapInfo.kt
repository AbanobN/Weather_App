package com.example.weatherapplication.ui.map

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.example.weatherapplication.R
import com.example.weatherapplication.databinding.CustomMapInfoBinding
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

class CustomMapInfo(
    mapView: MapView,
    private val context: Context
) : MarkerInfoWindow(R.layout.custom_map_info, mapView) { // Pass 0 for layout ID since we're using ViewBinding

    private var binding: CustomMapInfoBinding

    init {
        val inflater = LayoutInflater.from(context)
        binding = CustomMapInfoBinding.inflate(inflater, mapView, false)

        mView = binding.root
    }

    override fun onOpen(item: Any?) {
        val marker = item as Marker

        // Set the custom icon
        binding.infoWindowIcon.setImageResource(com.example.weatherapplication.R.drawable.ic_favorites)

        // Add click listener to the icon in the info window
        binding.infoWindowIcon.setOnClickListener {
            val position = marker.position
            val lat = position.latitude
            val lon = position.longitude

            // Show Toast with lat/lon when the icon is clicked
            Toast.makeText(context, "Lat: $lat, Lon: $lon", Toast.LENGTH_LONG).show()
        }
    }
}
