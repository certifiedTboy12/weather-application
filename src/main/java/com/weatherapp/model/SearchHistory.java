package com.weatherapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a successful weather search and its timestamp.
 */
public class SearchHistory {
    private final String city;
    private final LocalDateTime timestamp;

    public SearchHistory(String city) {
        this.city = city;
        this.timestamp = LocalDateTime.now();
    }

    public String getCity() {
        return city;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return city + "  •  " +
                timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
