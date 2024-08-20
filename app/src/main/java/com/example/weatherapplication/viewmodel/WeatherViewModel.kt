package com.example.weatherapplication.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.isOnline
import com.example.weatherapplication.model.ForecastItem
import com.example.weatherapplication.model.ForecastResponse
import com.example.weatherapplication.model.weatherResponseData
import com.example.weatherapplication.readFromJSONFile
import com.example.weatherapplication.repository.weatherRepository
import com.example.weatherapplication.saveToJSONFile
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherViewModel(
    private val repository: weatherRepository,
    private val context: Context // Pass the context for utility functions
) : ViewModel() {

    private val _weatherData = MutableLiveData<weatherResponseData?>()
    val weatherData: MutableLiveData<weatherResponseData?> get() = _weatherData

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _forecastData = MutableLiveData<ForecastResponse?>()
    val forecastData: MutableLiveData<ForecastResponse?> get() = _forecastData

    private val weatherFileName = "weather_data.json"
    private val forecastFileName = "forecast_data.json"

    fun fetchWeather(latitude: Double, longitude: Double, apiKey: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            if (isOnline(context)) {
                // Online: Fetch from API and store in local JSON
                try {
                    val response = repository.getCurrentWeather(latitude, longitude, apiKey)
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("WeatherViewModel", "Response Code: ${response.code()}")
                        Log.d("WeatherViewModel", "Response Body: $responseBody")
                        _weatherData.value = responseBody!!
                        saveToJSONFile(context, weatherFileName, responseBody)
                    } else {
                        Log.d("WeatherViewModel", "Error Response: ${response.errorBody()?.string()}")
                        _error.value = "Failed to load weather data: ${response.message()}"
                    }
                } catch (e: Exception) {
                    Log.d("WeatherViewModel", "Exception: ${e.message}")
                    _error.value = "An error occurred: ${e.message}"
                }
            } else {
                // Offline: Load from local JSON
                val cachedData = readFromJSONFile(context, weatherFileName, weatherResponseData::class.java)
                if (cachedData != null) {
                    _weatherData.value = cachedData
                } else {
                    _error.value = "No cached data available. Please connect to the internet."
                }
            }

            _loading.value = false
        }
    }

    fun fetchWeatherByCity(cityName: String, apiKey: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            if (isOnline(context)) {
                // Online: Fetch from API and store in local JSON
                try {
                    val response = repository.getCurrentWeatherByCity(cityName, apiKey)
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        _weatherData.value = responseBody
                        saveToJSONFile(context, weatherFileName, responseBody!!)
                    } else {
                        _error.value = "Failed to load weather data: ${response.message()}"
                    }
                } catch (e: Exception) {
                    _error.value = "An error occurred: ${e.message}"
                }
            } else {
                // Offline: Load from local JSON
                val cachedData = readFromJSONFile(context, weatherFileName, weatherResponseData::class.java)
                if (cachedData != null) {
                    _weatherData.value = cachedData
                } else {
                    _error.value = "No cached data available. Please connect to the internet."
                }
            }

            _loading.value = false
        }
    }

    fun fetchForecast(latitude: Double, longitude: Double, apiKey: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            if (isOnline(context)) {
                // Online: Fetch from API and store in local JSON
                try {
                    Log.d("WeatherViewModel", "Fetching forecast data for lat: $latitude, lon: $longitude")
                    val response = repository.getForecast(latitude, longitude, apiKey)
                    if (response.isSuccessful) {
                        val forecastResponse = response.body()
                        if (forecastResponse != null && forecastResponse.list.isNotEmpty()) {
                            Log.d("WeatherViewModel", "Forecast data fetched successfully")
                            Log.d("WeatherViewModel", "Forecast data size: ${forecastResponse.list.size}")
                            _forecastData.value = forecastResponse
                            saveToJSONFile(context, forecastFileName, forecastResponse)
                        } else {
                            Log.e("WeatherViewModel", "Forecast response is empty or list is empty")
                            _error.value = "No forecast data available"
                        }
                    } else {
                        Log.e("WeatherViewModel", "Error fetching forecast data: ${response.message()}")
                        _error.value = "Failed to load forecast data: ${response.message()}"
                    }
                } catch (e: Exception) {
                    Log.e("WeatherViewModel", "Exception during forecast data fetch: ${e.message}")
                    _error.value = "An error occurred: ${e.message}"
                }
            } else {
                // Offline: Load from local JSON
                val cachedData = readFromJSONFile(context, forecastFileName, ForecastResponse::class.java)
                if (cachedData != null) {
                    _forecastData.value = cachedData
                } else {
                    _error.value = "No cached data available. Please connect to the internet."
                }
            }

            _loading.value = false
        }
    }

    fun fetchForecastWeatherByCity(cityName: String, apiKey: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            if (isOnline(context)) {
                // Online: Fetch from API and store in local JSON
                try {
                    val response = repository.getForecastWeatherByCity(cityName, apiKey)
                    if (response.isSuccessful) {
                        val forecastResponse = response.body()
                        if (forecastResponse != null && forecastResponse.list.isNotEmpty()) {
                            Log.d("WeatherViewModel", "Forecast data fetched successfully")
                            Log.d("WeatherViewModel", "Forecast data size: ${forecastResponse.list.size}")
                            _forecastData.value = forecastResponse
                            saveToJSONFile(context, forecastFileName, forecastResponse)
                        } else {
                            Log.e("WeatherViewModel", "Forecast response is empty or list is empty")
                            _error.value = "No forecast data available"
                        }
                    } else {
                        Log.e("WeatherViewModel", "Error fetching forecast data: ${response.message()}")
                        _error.value = "Failed to load forecast data: ${response.message()}"
                    }
                } catch (e: Exception) {
                    Log.e("WeatherViewModel", "Exception during forecast data fetch: ${e.message}")
                    _error.value = "An error occurred: ${e.message}"
                }
            } else {
                // Offline: Load from local JSON
                val cachedData = readFromJSONFile(context, forecastFileName, ForecastResponse::class.java)
                if (cachedData != null) {
                    _forecastData.value = cachedData
                } else {
                    _error.value = "No cached data available. Please connect to the internet."
                }
            }

            _loading.value = false
        }
    }

    fun filterForecastsForToday(forecasts: List<ForecastItem>): List<ForecastItem> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = sdf.format(Date())
        return forecasts.filter { forecast ->
            val forecastDate = forecast.dtTxt.substring(0, 10)
            forecastDate == todayDate
        }
    }
}
