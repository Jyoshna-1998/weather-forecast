package com.example.weatherapplication

import com.example.weatherapplication.model.ForecastResponse
import com.example.weatherapplication.model.weatherResponseData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Response<weatherResponseData>

    @GET("data/2.5/weather")
    suspend fun getCurrentWeatherByCity(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String
    ): Response<weatherResponseData>

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Response<ForecastResponse>

    @GET("data/2.5/forecast")
    suspend fun getForecastWeatherByCity(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String
    ): Response<ForecastResponse>

}