package com.weatherapp.util;

import com.weatherapp.model.SearchHistory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Stores successful searches for display in the current application session.
 */
public class HistoryManager {

    private static final int MAX_HISTORY = 10;

    private final ObservableList<SearchHistory> history =
            FXCollections.observableArrayList();

    public void add(String city) {
        history.removeIf(item ->
                item.getCity().equalsIgnoreCase(city));

        history.add(0, new SearchHistory(city));

        if (history.size() > MAX_HISTORY) {
            history.remove(history.size() - 1);
        }
    }

    public ObservableList<SearchHistory> getHistory() {
        return history;
    }
}
