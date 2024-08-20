package com.example.weatherapplication.repository

import com.example.weatherapplication.ApiServices
import com.example.weatherapplication.model.ForecastResponse
import com.example.weatherapplication.model.weatherResponseData
import retrofit2.Response

class weatherRepository(private val apiService: ApiServices) {
    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): Response<weatherResponseData> {
        return apiService.getCurrentWeather(latitude, longitude, apiKey)
    }

    suspend fun getCurrentWeatherByCity(
        cityName: String,
        apiKey: String
    ): Response<weatherResponseData> {
        return apiService.getCurrentWeatherByCity(cityName, apiKey)
    }

    suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): Response<ForecastResponse> {
        return apiService.getForecast(latitude, longitude, apiKey)
    }

    suspend fun getForecastWeatherByCity(
        cityName: String,
        apiKey: String
    ): Response<ForecastResponse> {
        return apiService.getForecastWeatherByCity(cityName, apiKey)
    }


}
