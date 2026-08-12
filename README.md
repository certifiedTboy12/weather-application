# Weather Information App

A JavaFX desktop Weather Information App that retrieves current weather and
short-term forecast information from the OpenWeatherMap API.

## Features

- Search weather by city name
- Current temperature and feels-like temperature
- Humidity, pressure and wind speed
- Weather condition and weather icon
- Five-day short-term forecast
- Celsius/Fahrenheit temperature units
- m/s and mph wind units
- Search history with timestamps
- Dynamic day/evening/night backgrounds
- Input validation
- API and network error handling
- Responsive GUI using JavaFX Task
- API key stored in an environment variable
- Maven-based project

## Requirements

- JDK 17 or later
- Maven 3.8+
- Internet connection
- An OpenWeatherMap API key

## OpenWeatherMap API Key

Create an account at OpenWeatherMap and obtain an API key.

Set an environment variable named:

`OPENWEATHER_API_KEY`

### Windows PowerShell

```powershell
$env:OPENWEATHER_API_KEY="YOUR_API_KEY"
mvn javafx:run
```

### Windows Command Prompt

```cmd
set OPENWEATHER_API_KEY=YOUR_API_KEY
mvn javafx:run
```

### Linux/macOS

```bash
export OPENWEATHER_API_KEY="YOUR_API_KEY"
mvn javafx:run
```

Do not commit your API key to source control.

## Running the application

From the project directory:

```bash
mvn clean javafx:run
```

## Building the project

```bash
mvn clean package
```

## IntelliJ IDEA

1. Open the project as a Maven project.
2. Make sure the project SDK is JDK 17 or newer.
3. Set the `OPENWEATHER_API_KEY` environment variable in the Run Configuration.
4. Run `com.weatherapp.Main`, or execute the Maven goal `javafx:run`.

## Application Flow

1. Enter a city name.
2. Click Search or press Enter.
3. The application validates the input.
4. A background JavaFX Task sends requests to OpenWeatherMap.
5. The current weather and forecast are displayed.
6. The successful search is added to the history list.
7. The background changes according to sunrise, sunset and current time.
8. Change units using the unit selectors.

## API Endpoints

Current weather:

`https://api.openweathermap.org/data/2.5/weather`

Five-day forecast:

`https://api.openweathermap.org/data/2.5/forecast`

## Architecture

- `Main.java` - application entry point
- `WeatherApplication.java` - JavaFX GUI and application flow
- `WeatherApiService.java` - HTTP requests and JSON parsing
- `WeatherData.java` - current weather model
- `ForecastData.java` - forecast model
- `SearchHistory.java` - history model
- `HistoryManager.java` - in-memory search history
- `WeatherIconUtil.java` - weather icon mapping
- `weather.css` - JavaFX styling

## Error Handling

The application handles:

- Missing API key
- Empty city input
- API authentication errors
- Invalid city names
- API rate/server errors
- Network failures
- Invalid API responses

## Unit Conversion

OpenWeatherMap's `metric` units are used for Celsius and metres per second.
Its `imperial` units are used for Fahrenheit and miles per hour.

## Academic Notes

This project demonstrates:

- REST API integration
- HTTP communication in Java
- JSON parsing with Gson
- JavaFX GUI development
- Event-driven programming
- Multithreading with JavaFX Task
- Object-oriented design
- Input validation
- Exception handling
- Collection-based history tracking
- CSS-based GUI styling

## Author

Replace this section with your name and student information before submission.
