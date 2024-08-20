package com.example.weatherapplication

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.Target
import com.example.weatherapplication.NetworkModule.getRetrofitInstance
import com.example.weatherapplication.adapter.WeatherAdapter
import com.example.weatherapplication.databinding.ActivityMainBinding
import com.example.weatherapplication.repository.weatherRepository
import com.example.weatherapplication.viewModelfactory.WeatherViewModelFactory
import com.example.weatherapplication.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(
            weatherRepository(getRetrofitInstance().create(ApiServices::class.java)),
            this
        )
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            fetchLocation()
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkLocationPermission()) {
            fetchLocation()
        } else {
            requestLocationPermission()
        }

        setupObservers()
        val searchView: SearchView = findViewById(R.id.searchBarContainer)
        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(Color.WHITE)

        binding.searchBarContainer.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchBarContainer.clearFocus()
                query?.let {
                    val cityName = it.trim()
                    if (cityName.isNotEmpty()) {
                        viewModel.fetchWeatherByCity(
                            cityName = cityName,
                            apiKey = "a9d7877ecf28b142eab78b39d9e14c03"
                        )
                        viewModel.fetchForecastWeatherByCity(
                            cityName = cityName,
                            apiKey = "a9d7877ecf28b142eab78b39d9e14c03"
                        )
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Please enter a city name",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle real-time text change if needed
                return true
            }
        })
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun fetchLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.fetchWeather(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        apiKey = "a9d7877ecf28b142eab78b39d9e14c03"
                    )

                    // Call fetchForecast here
                    viewModel.fetchForecast(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        apiKey = "a9d7877ecf28b142eab78b39d9e14c03"
                    )
                } else {
                    Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun kelvinToCelsius(kelvin: Double): Double {
        return kelvin - 273.15
    }

    private fun setupObservers() {
        viewModel.weatherData.observe(this) { weatherResponse ->
            if (weatherResponse != null) {
                val tempFahrenheit = weatherResponse.main.temp
                val tempCelsius = kelvinToCelsius(tempFahrenheit)
                binding.tvTemperature.text = String.format("%.2f°C", tempCelsius)
                binding.tvDescription.text = weatherResponse.weather.firstOrNull()?.main

                binding.tvLocationName.text = weatherResponse.name

                val maxtempFahrenheit = weatherResponse.main.tempMax
                val maxtempCelsius = kelvinToCelsius(maxtempFahrenheit)
                binding.tvMaxTemp.text = String.format("Max temp %.2f°C", maxtempCelsius)
                val minTempFahrenheit = weatherResponse.main.tempMin
                val mintempCelsius = kelvinToCelsius(minTempFahrenheit)
                binding.tvMinTemp.text = String.format("Min temp %.2f°C", mintempCelsius)

                val iconUrl =
                    "https://openweathermap.org/img/wn/${weatherResponse.weather.firstOrNull()?.icon}.png"

                Glide.with(binding.icon.context)
                    .load(iconUrl)
                    .listener(object : com.bumptech.glide.request.RequestListener<Drawable> {


                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: com.bumptech.glide.load.DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .into(binding.icon)
            } else {
                Toast.makeText(this, "No weather data available", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.forecastData.observe(this) { forecastResponse ->
            if (forecastResponse != null && forecastResponse.list.isNotEmpty()) {
                // Filter forecasts for today
                val todayForecasts = viewModel.filterForecastsForToday(forecastResponse.list)
                val dateFormat = SimpleDateFormat(
                    "EEEE, MMMM d, yyyy",
                    Locale.getDefault()
                ) // Example: "Tuesday, August 22, 2023"
                val todayDate = dateFormat.format(Date())
                binding.tvDateValue.text =
                    todayDate                // Set up RecyclerView with WeatherAdapter
                binding.recyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding.recyclerView.adapter = WeatherAdapter(todayForecasts)
            } else {
                Log.e("MainActivity", "Forecast data is empty or not available")
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
