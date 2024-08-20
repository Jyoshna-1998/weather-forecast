package com.example.weatherapplication.viewModelfactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapplication.repository.weatherRepository
import com.example.weatherapplication.viewmodel.WeatherViewModel

class WeatherViewModelFactory(
    private val repository: weatherRepository,
    private val context: Context // Add Context parameter
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(repository, context) as T // Pass context to ViewModel
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
