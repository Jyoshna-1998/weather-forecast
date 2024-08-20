package com.example.weatherapplication

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter

// Utility function to check network connectivity
fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

// Utility function to save data to a JSON file
fun saveToJSONFile(context: Context, fileName: String, data: Any) {
    val gson = Gson()
    val json = gson.toJson(data)
    val file = File(context.filesDir, fileName)
    FileWriter(file).use { writer ->
        writer.write(json)
    }
}

// Utility function to read data from a JSON file
fun <T> readFromJSONFile(context: Context, fileName: String, type: Class<T>): T? {
    val file = File(context.filesDir, fileName)
    return if (file.exists()) {
        val gson = Gson()
        FileReader(file).use { reader ->
            gson.fromJson(reader, type)
        }
    } else {
        null
    }
}
