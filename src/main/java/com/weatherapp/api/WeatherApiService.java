package com.weatherapp.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weatherapp.model.ForecastData;
import com.weatherapp.model.WeatherData;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles communication with OpenWeatherMap.
 */
public class WeatherApiService {

        private static final String CURRENT_ENDPOINT = "https://api.openweathermap.org/data/2.5/weather";

        private static final String FORECAST_ENDPOINT = "https://api.openweathermap.org/data/2.5/forecast";

        private final HttpClient httpClient;
        private final String apiKey;

        public WeatherApiService() {
                this.httpClient = HttpClient.newHttpClient();
                this.apiKey = System.getenv("OPENWEATHER_API_KEY");
        }

        public WeatherData getCurrentWeather(String city, String units)
                        throws IOException, InterruptedException {

                validateApiKey();

                String encodedCity = URLEncoder.encode(
                                city.trim(), StandardCharsets.UTF_8);

                String url = CURRENT_ENDPOINT
                                + "?q=" + encodedCity
                                + "&units=" + units
                                + "&appid=" + apiKey;

                HttpResponse<String> response = sendRequest(url);
                JsonObject root = parseResponse(response);

                String name = root.get("name").getAsString();
                String country = root.getAsJsonObject("sys")
                                .get("country").getAsString();

                JsonObject main = root.getAsJsonObject("main");
                JsonObject wind = root.getAsJsonObject("wind");
                JsonObject weather = root.getAsJsonArray("weather")
                                .get(0).getAsJsonObject();
                JsonObject sys = root.getAsJsonObject("sys");

                return new WeatherData(
                                name,
                                country,
                                main.get("temp").getAsDouble(),
                                main.get("feels_like").getAsDouble(),
                                main.get("humidity").getAsInt(),
                                wind.get("speed").getAsDouble(),
                                main.get("pressure").getAsInt(),
                                weather.get("main").getAsString(),
                                weather.get("description").getAsString(),
                                weather.get("icon").getAsString(),
                                sys.get("sunrise").getAsLong(),
                                sys.get("sunset").getAsLong());
        }

        public List<ForecastData> getForecast(String city, String units)
                        throws IOException, InterruptedException {

                validateApiKey();

                String encodedCity = URLEncoder.encode(
                                city.trim(), StandardCharsets.UTF_8);

                String url = FORECAST_ENDPOINT
                                + "?q=" + encodedCity
                                + "&units=" + units
                                + "&appid=" + apiKey;

                HttpResponse<String> response = sendRequest(url);
                JsonObject root = parseResponse(response);

                List<ForecastData> forecast = new ArrayList<>();
                JsonArray list = root.getAsJsonArray("list");

                /*
                 * OpenWeatherMap provides entries at three-hour intervals.
                 * We select the first forecast entry for each of the next
                 * five calendar days.
                 */
                String lastDate = "";

                for (JsonElement element : list) {
                        JsonObject item = element.getAsJsonObject();

                        LocalDateTime dateTime = LocalDateTime.parse(
                                        item.get("dt_txt").getAsString().replace(" ", "T"));

                        String date = dateTime.toLocalDate().toString();

                        if (date.equals(lastDate)) {
                                continue;
                        }

                        JsonObject main = item.getAsJsonObject("main");
                        JsonObject wind = item.getAsJsonObject("wind");
                        JsonObject weather = item.getAsJsonArray("weather")
                                        .get(0).getAsJsonObject();

                        forecast.add(new ForecastData(
                                        dateTime,
                                        main.get("temp").getAsDouble(),
                                        main.get("feels_like").getAsDouble(),
                                        main.get("humidity").getAsInt(),
                                        wind.get("speed").getAsDouble(),
                                        weather.get("main").getAsString(),
                                        weather.get("description").getAsString(),
                                        weather.get("icon").getAsString()));

                        lastDate = date;

                        if (forecast.size() == 5) {
                                break;
                        }
                }

                return forecast;
        }

        private HttpResponse<String> sendRequest(String url)
                        throws IOException, InterruptedException {

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .header("Accept", "application/json")
                                .GET()
                                .build();

                return httpClient.send(
                                request,
                                HttpResponse.BodyHandlers.ofString());
        }

        private JsonObject parseResponse(HttpResponse<String> response)
                        throws IOException {

                int status = response.statusCode();

                if (status == 401) {
                        throw new IOException(
                                        "Invalid or missing OpenWeatherMap API key.");
                }

                if (status == 404) {
                        throw new IOException(
                                        "Location not found. Please check the city name.");
                }

                if (status == 429) {
                        throw new IOException(
                                        "API request limit reached. Please try again later.");
                }

                if (status >= 500) {
                        throw new IOException(
                                        "Weather service is temporarily unavailable.");
                }

                if (status < 200 || status >= 300) {
                        throw new IOException(
                                        "Weather request failed with HTTP status " + status);
                }

                try {
                        return JsonParser.parseString(response.body()).getAsJsonObject();
                } catch (Exception e) {
                        throw new IOException(
                                        "The weather service returned an invalid response.", e);
                }
        }

        private void validateApiKey() throws IOException {
                if (apiKey == null || apiKey.isBlank()) {
                        throw new IOException(
                                        "OPENWEATHER_API_KEY environment variable is not set.");
                }
        }
}
