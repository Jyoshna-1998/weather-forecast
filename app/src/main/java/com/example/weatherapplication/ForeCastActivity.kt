package com.example.weatherapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapplication.NetworkModule.getRetrofitInstance
import com.example.weatherapplication.databinding.ActivityForeCastBinding
import com.example.weatherapplication.repository.weatherRepository
import com.example.weatherapplication.viewModelfactory.WeatherViewModelFactory
import com.example.weatherapplication.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.example.weatherapplication.BuildConfig
import com.example.weatherapplication.adapter.ForeCastAdapter

class ForeCastActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForeCastBinding
    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(
            weatherRepository(getRetrofitInstance().create(ApiServices::class.java)),
            this
        )
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForeCastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val cityName = intent.getStringExtra("CITY_NAME")
        if (!cityName.isNullOrEmpty()) {
            // Fetch forecast data based on the city name
            viewModel.fetchForecastWeatherByCity(
                cityName = cityName,
                apiKey = BuildConfig.apiKey
            )
        } else {
            // If city name is not provided, fallback to location-based fetching
            requestLocationPermission()
        }
        //requestLocationPermission() // Request location permission and fetch location
        viewModel.forecastData.observe(this) { forecastResponse ->
            if (forecastResponse != null && forecastResponse.list.isNotEmpty()) {
                val adapter = ForeCastAdapter(forecastResponse.list)
                binding.recyclerView.adapter = adapter
            } else {
                Toast.makeText(this, "No forecast data available", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            fetchLocation() // Call fetchLocation if permission is already granted
        }
    }

    private fun fetchLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.fetchForecast(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        apiKey = BuildConfig.apiKey
                    )
                } else {
                    Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
