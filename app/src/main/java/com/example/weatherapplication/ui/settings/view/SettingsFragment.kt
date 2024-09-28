package com.example.weatherapplication.ui.settings.view

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weatherapplication.MainActivity
import com.example.weatherapplication.R
import com.example.weatherapplication.data.localdatasource.database.AppDatabase
import com.example.weatherapplication.data.localdatasource.localdatsource.LocalDataSource
import com.example.weatherapplication.data.localdatasource.sharedpreferences.SharedPreferences
import com.example.weatherapplication.data.remotedatasource.remotedatasource.RemoteDataSource
import com.example.weatherapplication.data.repository.WeatherRepository
import com.example.weatherapplication.databinding.FragmentSettingsBinding
import com.example.weatherapplication.databinding.NonetworkAlertBinding
import com.example.weatherapplication.ui.settings.viewmodel.SettingViewModelFactory
import com.example.weatherapplication.ui.settings.viewmodel.SettingsViewModel
import com.example.weatherapplication.utiltes.InternetState
import kotlinx.coroutines.launch
import android.Manifest

class SettingsFragment : Fragment() {

    private lateinit var _binding: FragmentSettingsBinding
    private lateinit var  settingsViewModel: SettingsViewModel
    private var isNetwork = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val remoteDataSource = RemoteDataSource()
        val localDataSource = LocalDataSource(
            AppDatabase.getDatabase(requireContext()),
            SharedPreferences(requireContext())
        )

        val internetState = InternetState(requireActivity().application)
        settingsViewModel = ViewModelProvider(this, SettingViewModelFactory(WeatherRepository(remoteDataSource,localDataSource),internetState)).get(
            SettingsViewModel::class.java)


        lifecycleScope.launch {
            settingsViewModel.isInternetAvailable.collect { isAvailable ->
                if (isAvailable) {
                    isNetwork = true
                } else {
                    isNetwork = false
                }
            }
        }
        settingsViewModel.observeNetwork()

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = _binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsViewModel.getSettings()

        lifecycleScope.launch{
            settingsViewModel.settings.collect { settings ->
                updateRadioGroups(settings)
            }
        }

        _binding.groupLocation.setOnCheckedChangeListener{ group , checkedId ->
            when(checkedId){
                R.id.map -> {
                    if(isNetwork)
                    {
                        settingsViewModel.saveLocation("map")

                        val bundle = Bundle().apply{
                            putString("comeFrom","Setting")
                        }

                        findNavController().navigate(R.id.action_nav_settings_to_mapFragment,bundle)
                    }
                    else{
                        val binding = NonetworkAlertBinding.inflate(LayoutInflater.from(context))
                        val dialog = AlertDialog.Builder(context)
                            .setView(binding.root)
                            .create()

                        binding.btnConfirm.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }

                }
                else -> {
                    settingsViewModel.saveLocation("gps")

                    val bundle = Bundle().apply {
                        putFloat("lat", 0.0f)
                        putFloat("lon", 0.0f)
                    }

                    findNavController().navigate(R.id.action_nav_settings_to_nav_home,bundle)
                }
            }

        }

        _binding.groupLanguage.setOnCheckedChangeListener{ _ , checkedId ->
            when(checkedId){
                R.id.arabic -> {
                    settingsViewModel.saveLan("ar")
                    (requireActivity() as MainActivity).checkAndChangLocality()
                }
                else -> {
                    settingsViewModel.saveLan("en")
                    (requireActivity() as MainActivity).checkAndChangLocality()
                }

            }
            if(!isNetwork){
                val binding = NonetworkAlertBinding.inflate(LayoutInflater.from(context))
                binding.tvMessage.text = getString(R.string.no_network_lang)

                val dialog = AlertDialog.Builder(context)
                    .setView(binding.root)
                    .create()

                binding.btnConfirm.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            }
        }

        _binding.groupTemperature.setOnCheckedChangeListener{ _ , checkedId ->
            when(checkedId){
                R.id.kelvin -> settingsViewModel.saveUnit("K")
                R.id.fahrenheit -> settingsViewModel.saveUnit("F")
                else -> settingsViewModel.saveUnit("C")
            }
        }

        _binding.groupWindSpeed.setOnCheckedChangeListener{ _ , checkedId ->
            when(checkedId){
                R.id.mile_hour -> settingsViewModel.saveSpeed("mph")
                else -> settingsViewModel.saveSpeed("mps")
            }
        }

        _binding.groupNotifications.setOnCheckedChangeListener{ _ , checkedId ->
            when(checkedId){
                R.id.disable -> {
                    settingsViewModel.saveNotification("disable")
                    disableNotifications()
                    Toast.makeText(requireContext(), "Notifications disabled", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    settingsViewModel.saveNotification("enable")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestNotificationPermission()
                    } else {
                        Toast.makeText(requireContext(), "Notifications enabled", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateRadioGroups(settings: List<String>) {
        when (settings[0]) {
            "gps" -> _binding.groupLocation.check(R.id.gps)
            "map" -> _binding.groupLocation.check(R.id.map)
        }
        when (settings[1]) {
            "en" -> _binding.groupLanguage.check(R.id.english)
            "ar" -> _binding.groupLanguage.check(R.id.arabic)
        }
        when (settings[2]) {
            "C" -> _binding.groupTemperature.check(R.id.celsius)
            "K" -> _binding.groupTemperature.check(R.id.kelvin)
            "F" -> _binding.groupTemperature.check(R.id.fahrenheit)
        }
        when (settings[3]) {
            "mps" -> _binding.groupWindSpeed.check(R.id.meter_sec)
            "mph" -> _binding.groupWindSpeed.check(R.id.mile_hour)
        }
        when (settings[4]) {
            "enable" -> _binding.groupNotifications.check(R.id.enable)
            "disable" -> _binding.groupNotifications.check(R.id.disable)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1002
            )
        } else {
            Toast.makeText(requireContext(), "Notification Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }
    private fun disableNotifications() {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }


}