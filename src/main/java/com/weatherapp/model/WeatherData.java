package com.weatherapp.model;

/**
 * Represents the current weather information returned by OpenWeatherMap.
 */
public class WeatherData {
    private final String city;
    private final String country;
    private final double temperature;
    private final double feelsLike;
    private final int humidity;
    private final double windSpeed;
    private final int pressure;
    private final String condition;
    private final String description;
    private final String iconCode;
    private final long sunrise;
    private final long sunset;

    public WeatherData(String city, String country, double temperature,
                       double feelsLike, int humidity, double windSpeed,
                       int pressure, String condition, String description,
                       String iconCode, long sunrise, long sunset) {
        this.city = city;
        this.country = country;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.pressure = pressure;
        this.condition = condition;
        this.description = description;
        this.iconCode = iconCode;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public String getCity() { return city; }
    public String getCountry() { return country; }
    public double getTemperature() { return temperature; }
    public double getFeelsLike() { return feelsLike; }
    public int getHumidity() { return humidity; }
    public double getWindSpeed() { return windSpeed; }
    public int getPressure() { return pressure; }
    public String getCondition() { return condition; }
    public String getDescription() { return description; }
    public String getIconCode() { return iconCode; }
    public long getSunrise() { return sunrise; }
    public long getSunset() { return sunset; }
}
