package com.weatherapp;

import com.weatherapp.api.WeatherApiService;
import com.weatherapp.model.ForecastData;
import com.weatherapp.model.SearchHistory;
import com.weatherapp.model.WeatherData;
import com.weatherapp.util.HistoryManager;
import com.weatherapp.util.WeatherIconUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Main JavaFX user interface for the Weather Information App.
 */
public class WeatherApplication extends Application {

    private final WeatherApiService apiService = new WeatherApiService();
    private final HistoryManager historyManager = new HistoryManager();

    private BorderPane root;
    private TextField cityField;
    private Button searchButton;
    private ComboBox<String> temperatureUnit;
    private ComboBox<String> windUnit;
    private Label statusLabel;

    private Label locationLabel;
    private Label temperatureLabel;
    private Label conditionLabel;
    private Label humidityLabel;
    private Label windLabel;
    private Label feelsLikeLabel;
    private Label pressureLabel;
    private Text currentWeatherIcon;

    private HBox forecastContainer;
    private ListView<SearchHistory> historyList;

    private WeatherData currentWeather;
    private List<ForecastData> currentForecast;
    private boolean busy;

    @Override
    public void start(Stage stage) {
        root = new BorderPane();
        root.getStyleClass().add("app-root");

        root.setTop(createHeader());
        root.setCenter(createMainContent());
        root.setBottom(createStatusBar());

        Scene scene = new Scene(root, 1180, 820);

        String css = getClass()
                .getResource("/styles/weather.css")
                .toExternalForm();
        scene.getStylesheets().add(css);

        cityField.setOnAction(event -> searchWeather());
        searchButton.setOnAction(event -> searchWeather());

        temperatureUnit.setOnAction(event -> refreshDisplayedUnits());
        windUnit.setOnAction(event -> refreshDisplayedUnits());

        stage.setTitle("Weather Information App");
        stage.setScene(scene);
        stage.setMinWidth(980);
        stage.setMinHeight(700);
        stage.show();

        cityField.requestFocus();
    }

    private VBox createHeader() {
        VBox header = new VBox(14);
        header.getStyleClass().add("header");
        header.setPadding(new Insets(24, 30, 24, 30));

        Label title = new Label("Weather Information");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label(
                "Real-time weather information and short-term forecast");
        subtitle.getStyleClass().add("subtitle");

        cityField = new TextField();
        cityField.setPromptText("Enter city name e.g. Lagos, London, Abuja");
        cityField.getStyleClass().add("city-field");
        HBox.setHgrow(cityField, Priority.ALWAYS);

        searchButton = new Button("Search");
        searchButton.getStyleClass().add("search-button");
        searchButton.setMinWidth(110);

        HBox searchRow = new HBox(12, cityField, searchButton);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        temperatureUnit = new ComboBox<>();
        temperatureUnit.getItems().addAll("Celsius (°C)", "Fahrenheit (°F)");
        temperatureUnit.setValue("Celsius (°C)");
        temperatureUnit.getStyleClass().add("unit-box");

        windUnit = new ComboBox<>();
        windUnit.getItems().addAll("m/s", "mph");
        windUnit.setValue("m/s");
        windUnit.getStyleClass().add("unit-box");

        Label tempText = new Label("Temperature:");
        Label windText = new Label("Wind:");

        HBox unitsRow = new HBox(
                10, tempText, temperatureUnit,
                new Region(), windText, windUnit);
        unitsRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(unitsRow.getChildren().get(2), Priority.ALWAYS);

        header.getChildren().addAll(title, subtitle, searchRow, unitsRow);
        return header;
    }

    private ScrollPane createMainContent() {
        VBox content = new VBox(22);
        content.setPadding(new Insets(24, 30, 24, 30));

        VBox currentCard = createCurrentWeatherCard();
        VBox forecastCard = createForecastCard();
        VBox historyCard = createHistoryCard();

        content.getChildren().addAll(currentCard, forecastCard, historyCard);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("main-scroll");
        return scrollPane;
    }

    private VBox createCurrentWeatherCard() {
        VBox card = new VBox(18);
        card.getStyleClass().add("card");

        locationLabel = new Label("Search for a city");
        locationLabel.getStyleClass().add("location-title");

        HBox weatherMain = new HBox(30);
        weatherMain.setAlignment(Pos.CENTER_LEFT);

        currentWeatherIcon = WeatherIconUtil.createIcon("01d", 72);

        temperatureLabel = new Label("--°");
        temperatureLabel.getStyleClass().add("temperature");

        conditionLabel = new Label("No weather data");
        conditionLabel.getStyleClass().add("condition");

        VBox temperatureBox = new VBox(4,
                temperatureLabel, conditionLabel);
        temperatureBox.setAlignment(Pos.CENTER_LEFT);

        weatherMain.getChildren().addAll(
                currentWeatherIcon, temperatureBox);

        GridPane details = new GridPane();
        details.setHgap(14);
        details.setVgap(12);

        humidityLabel = createDetailLabel("Humidity: --");
        windLabel = createDetailLabel("Wind: --");
        feelsLikeLabel = createDetailLabel("Feels like: --");
        pressureLabel = createDetailLabel("Pressure: --");

        details.add(humidityLabel, 0, 0);
        details.add(windLabel, 1, 0);
        details.add(feelsLikeLabel, 2, 0);
        details.add(pressureLabel, 3, 0);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25);
            details.getColumnConstraints().add(cc);
        }

        card.getChildren().addAll(locationLabel, weatherMain, details);
        return card;
    }

    private Label createDetailLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("detail-label");
        return label;
    }

    private VBox createForecastCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");

        Label title = new Label("5-Day Forecast");
        title.getStyleClass().add("section-title");

        forecastContainer = new HBox(12);
        forecastContainer.setAlignment(Pos.CENTER);

        for (int i = 0; i < 5; i++) {
            forecastContainer.getChildren().add(
                    createForecastPlaceholder());
        }

        card.getChildren().addAll(title, forecastContainer);
        return card;
    }

    private VBox createForecastPlaceholder() {
        VBox box = new VBox(8);
        box.getStyleClass().add("forecast-card");
        box.setAlignment(Pos.CENTER);
        HBox.setHgrow(box, Priority.ALWAYS);

        Label day = new Label("--");
        Label icon = new Label("•");
        Label temp = new Label("--°");
        Label condition = new Label("Waiting");

        day.getStyleClass().add("forecast-day");
        icon.getStyleClass().add("forecast-icon");
        temp.getStyleClass().add("forecast-temperature");
        condition.getStyleClass().add("forecast-condition");

        box.getChildren().addAll(day, icon, temp, condition);
        return box;
    }

    private VBox createHistoryCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");

        Label title = new Label("Recent Search History");
        title.getStyleClass().add("section-title");

        historyList = new ListView<>(historyManager.getHistory());
        historyList.setPlaceholder(
                new Label("Your successful searches will appear here."));
        historyList.setPrefHeight(145);

        historyList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                SearchHistory selected = historyList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    cityField.setText(selected.getCity());
                    searchWeather();
                }
            }
        });

        card.getChildren().addAll(title, historyList);
        return card;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(10, 30, 10, 30));
        statusBar.getStyleClass().add("status-bar");

        statusLabel = new Label(
                "Ready. Enter a city to retrieve weather information.");
        statusLabel.getStyleClass().add("status-text");

        statusBar.getChildren().add(statusLabel);
        return statusBar;
    }

    private void searchWeather() {
        if (busy) {
            return;
        }

        String city = cityField.getText().trim();

        if (city.isEmpty()) {
            showError("Please enter a city name.");
            cityField.requestFocus();
            return;
        }

        if (city.length() < 2) {
            showError("Please enter a valid city name.");
            return;
        }

        setBusy(true);
        statusLabel.setText("Retrieving weather information...");

        String units = getApiUnits();

        Task<WeatherResult> task = new Task<>() {
            @Override
            protected WeatherResult call() throws Exception {
                WeatherData weather = apiService.getCurrentWeather(city, units);
                List<ForecastData> forecast = apiService.getForecast(city, units);
                return new WeatherResult(weather, forecast);
            }
        };

        task.setOnSucceeded(event -> {
            WeatherResult result = task.getValue();

            currentWeather = result.weather();
            currentForecast = result.forecast();

            updateCurrentWeather();
            updateForecast();
            historyManager.add(currentWeather.getCity());

            setBusy(false);
            statusLabel.setText(
                    "Weather updated successfully at "
                            + LocalDateTime.now()
                                    .format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        });

        task.setOnFailed(event -> {
            Throwable error = task.getException();
            String message = error != null
                    ? friendlyError(error)
                    : "Unable to retrieve weather information.";

            showError(message);
            setBusy(false);
        });

        Thread thread = new Thread(task, "weather-api-thread");
        thread.setDaemon(true);
        thread.start();
    }

    private String friendlyError(Throwable error) {
        String message = error.getMessage();

        if (message == null || message.isBlank()) {
            return "Unable to retrieve weather information.";
        }

        if (message.contains("OPENWEATHER_API_KEY")) {
            return "API key is missing. Set OPENWEATHER_API_KEY and restart the application.";
        }

        if (message.toLowerCase().contains("connect")
                || message.toLowerCase().contains("network")) {
            return "Network connection failed. Check your internet connection.";
        }

        return message;
    }

    private void updateCurrentWeather() {
        if (currentWeather == null) {
            return;
        }

        String temperatureSymbol = temperatureUnit.getValue().startsWith("C") ? "°C" : "°F";

        locationLabel.setText(
                currentWeather.getCity()
                        + ", " + currentWeather.getCountry());

        temperatureLabel.setText(
                format(currentWeather.getTemperature())
                        + temperatureSymbol);

        conditionLabel.setText(
                capitalize(currentWeather.getDescription()));

        humidityLabel.setText(
                "Humidity: " + currentWeather.getHumidity() + "%");

        feelsLikeLabel.setText(
                "Feels like: "
                        + format(currentWeather.getFeelsLike())
                        + temperatureSymbol);

        pressureLabel.setText(
                "Pressure: " + currentWeather.getPressure() + " hPa");

        windLabel.setText(
                "Wind: " + format(currentWeather.getWindSpeed())
                        + " " + getWindUnit());

        currentWeatherIcon = WeatherIconUtil.createIcon(
                currentWeather.getIconCode(), 72);

        BorderPane weatherParent = (BorderPane) root.getTop().getParent();

        /*
         * The icon is inside the current card. Rebuild the current card
         * would be unnecessarily expensive, so locate its weather row.
         */
        refreshWeatherIcon();

        updateBackground();
    }

    private void refreshWeatherIcon() {
        if (root.getCenter() instanceof ScrollPane scrollPane) {
            if (scrollPane.getContent() instanceof VBox content
                    && !content.getChildren().isEmpty()
                    && content.getChildren().get(0) instanceof VBox card) {

                if (card.getChildren().size() > 1
                        && card.getChildren().get(1) instanceof HBox row) {

                    row.getChildren().set(0, currentWeatherIcon);
                }
            }
        }
    }

    private void updateForecast() {
        if (currentForecast == null) {
            return;
        }

        forecastContainer.getChildren().clear();

        for (ForecastData data : currentForecast) {
            forecastContainer.getChildren().add(
                    createForecastCard(data));
        }
    }

    private VBox createForecastCard(ForecastData data) {
        VBox box = new VBox(8);
        box.getStyleClass().add("forecast-card");
        box.setAlignment(Pos.CENTER);
        HBox.setHgrow(box, Priority.ALWAYS);

        String tempSymbol = temperatureUnit.getValue().startsWith("C") ? "°C" : "°F";

        Label day = new Label(
                data.getDateTime().format(
                        DateTimeFormatter.ofPattern("EEE, dd MMM")));

        Text icon = WeatherIconUtil.createIcon(
                data.getIconCode(), 38);

        Label temp = new Label(
                format(data.getTemperature()) + tempSymbol);

        Label condition = new Label(
                capitalize(data.getDescription()));

        Label humidity = new Label(
                "Humidity " + data.getHumidity() + "%");

        day.getStyleClass().add("forecast-day");
        temp.getStyleClass().add("forecast-temperature");
        condition.getStyleClass().add("forecast-condition");
        humidity.getStyleClass().add("forecast-small");

        box.getChildren().addAll(
                day, icon, temp, condition, humidity);

        return box;
    }

    private void refreshDisplayedUnits() {
        if (currentWeather != null) {
            searchWeather();
        }
    }

    private String getApiUnits() {
        return temperatureUnit.getValue().startsWith("C")
                ? "metric"
                : "imperial";
    }

    private String getWindUnit() {
        return windUnit.getValue().equals("mph")
                ? "mph"
                : "m/s";
    }

    private String format(double value) {
        return String.format("%.1f", value);
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return Character.toUpperCase(value.charAt(0))
                + value.substring(1);
    }

    private void updateBackground() {
        if (currentWeather == null) {
            return;
        }

        long now = System.currentTimeMillis() / 1000;
        long sunrise = currentWeather.getSunrise();
        long sunset = currentWeather.getSunset();

        root.getStyleClass().removeAll(
                "day-background",
                "evening-background",
                "night-background");

        if (now < sunrise) {
            root.getStyleClass().add("night-background");
        } else if (now < sunset) {
            root.getStyleClass().add("day-background");
        } else {
            root.getStyleClass().add("evening-background");
        }
    }

    private void showError(String message) {
        statusLabel.setText(message);

        Alert alert = new Alert(
                Alert.AlertType.ERROR,
                message,
                ButtonType.OK);
        alert.setTitle("Weather Information App");
        alert.setHeaderText("Unable to retrieve weather");
        alert.showAndWait();
    }

    private void setBusy(boolean value) {
        busy = value;
        searchButton.setDisable(value);
        cityField.setDisable(value);
        searchButton.setText(value ? "Loading..." : "Search");
    }

    private record WeatherResult(
            WeatherData weather,
            List<ForecastData> forecast) {
    }

    @Override
    public void stop() {
        Platform.exit();
    }
}
