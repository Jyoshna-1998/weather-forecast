# weather-forecast

This is a weather application that fetches and displays the current weather for the user's location using the OpenWeatherMap API. The app supports both online and offline functionality, ensuring that the last seen weather information, including images and icons, is available when the device is offline.

## Features

- Displays current weather data for the user's current location using the OpenWeatherMap API.
- Creative and intuitive UI design with contextually relevant images and icons.
- Supports offline functionality by caching the last seen weather data.
- Built using the MVVM architecture pattern with Retrofit, Kotlin Coroutines, and LiveData.
-  XML for UI design, following modern Android development best practices.
- search functionality based on city and 5days forecast data. 
## Tech Stack

- **Language:** Kotlin
- **Architecture:** MVVM (Model-View-ViewModel)
- **UI Design:** XML 
- **Networking:** Retrofit
- **Asynchronous Tasks:** Kotlin Coroutines
- **Data Binding:** LiveData
- **Location Services:** FusedLocationProviderClient
- **Caching:** save data in json format and read data from json when offline (for storing the last seen weather data)

## Screenshots

Splash Screen:

The splash screen displays a welcoming graphic or logo while the app initializes. It ensures a smooth transition to the main content.

![Screenshot 2024-08-21 160607](https://github.com/user-attachments/assets/91c21833-979a-4460-99e3-dd7f156d97c4)

Location Access

![image](https://github.com/user-attachments/assets/eb5ebcc5-63a8-4a04-912a-be2b02b97778)


Main Screen:
The main screen shows the current weather for the user’s location, including temperature, humidity, weather conditions, and a relevant icon.

![Screenshot 2024-08-21 160702](https://github.com/user-attachments/assets/f80253b9-4903-473d-89d4-7c765ee7f70d)

Search Screen:

Allows users to search for weather information by entering a city name. The UI is intuitive and designed for quick searches with minimal input.

![Screenshot 2024-08-21 160903](https://github.com/user-attachments/assets/1fc80cf3-d003-447b-8c5f-9b01065578c0)

5-Day Weather Forecast:

Displays a detailed weather forecast for the next five days, showing temperature, weather conditions, and time-specific details in an easy-to-read format.

![Screenshot 2024-08-21 160937](https://github.com/user-attachments/assets/600ddc01-afb2-442a-bebe-e24c1a25db62)


Offline Data Handling:

The app caches the most recent weather data so that users can still view the last fetched weather information when offline. This includes weather icons and conditions.

![Screenshot 2024-08-21 161539](https://github.com/user-attachments/assets/f11b1c7a-6dd5-45ce-9531-30aef700befa)

#**apikey secure**
# local properties file
local.properties
add this in apiKey="apikey"

## How to Run the Project

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/weather-app.git
   cd weather-app
**APK file**
https://drive.google.com/file/d/1kY6CuPM8UjW-ji42RSNM-GC9HSPWW1tb/view?usp=sharing
