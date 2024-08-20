package com.example.weatherapplication.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.Target
import com.example.weatherapplication.databinding.ItemForecastBinding
import com.example.weatherapplication.model.ForecastItem
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherAdapter(private val forecastList: List<ForecastItem>) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    inner class WeatherViewHolder(private val binding: ItemForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(forecast: ForecastItem) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = inputFormat.parse(forecast.dtTxt)
            val formattedTime = date?.let { outputFormat.format(it) } ?: "N/A"

            binding.time.text = formattedTime
            val tempFahrenheit = forecast.main.temp
            val tempCelsius = kelvinToCelsius(tempFahrenheit)
            binding.temperature.text = String.format("%.2f°C", tempCelsius)
            val iconUrl =
                "https://openweathermap.org/img/wn/${forecast.weather.firstOrNull()?.icon}.png"

            Glide.with(binding.weatherIcon.context)
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
                .into(binding.weatherIcon)

        }

        private fun kelvinToCelsius(kelvin: Double): Double {
            return kelvin - 273.15
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding =
            ItemForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(forecastList[position])
    }

    override fun getItemCount(): Int = forecastList.size
}
