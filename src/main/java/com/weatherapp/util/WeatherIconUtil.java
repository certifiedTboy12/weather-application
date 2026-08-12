package com.weatherapp.util;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Provides simple emoji-based weather icons. This avoids external
 * image dependencies while still giving the interface a visual
 * representation of weather conditions.
 */
public final class WeatherIconUtil {

    private WeatherIconUtil() {
    }

    public static Text createIcon(String iconCode, double size) {
        String icon = getIcon(iconCode);
        Text text = new Text(icon);
        text.setFont(Font.font("System", size));
        return text;
    }

    public static String getIcon(String iconCode) {
        if (iconCode == null || iconCode.isBlank()) {
            return "🌤";
        }

        return switch (iconCode.substring(0, 2)) {
            case "01" -> iconCode.endsWith("n") ? "🌙" : "☀";
            case "02" -> iconCode.endsWith("n") ? "☁" : "🌤";
            case "03", "04" -> "☁";
            case "09" -> "🌧";
            case "10" -> iconCode.endsWith("n") ? "🌧" : "🌦";
            case "11" -> "⛈";
            case "13" -> "❄";
            case "50" -> "🌫";
            default -> "🌤";
        };
    }
}
