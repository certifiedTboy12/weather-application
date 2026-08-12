package com.weatherapp.model;

import java.time.LocalDateTime;

/**
 * Represents one forecast entry.
 */
public class ForecastData {
    private final LocalDateTime dateTime;
    private final double temperature;
    private final double feelsLike;
    private final int humidity;
    private final double windSpeed;
    private final String condition;
    private final String description;
    private final String iconCode;

    public ForecastData(LocalDateTime dateTime, double temperature,
                        double feelsLike, int humidity, double windSpeed,
                        String condition, String description, String iconCode) {
        this.dateTime = dateTime;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.condition = condition;
        this.description = description;
        this.iconCode = iconCode;
    }

    public LocalDateTime getDateTime() { return dateTime; }
    public double getTemperature() { return temperature; }
    public double getFeelsLike() { return feelsLike; }
    public int getHumidity() { return humidity; }
    public double getWindSpeed() { return windSpeed; }
    public String getCondition() { return condition; }
    public String getDescription() { return description; }
    public String getIconCode() { return iconCode; }
}
