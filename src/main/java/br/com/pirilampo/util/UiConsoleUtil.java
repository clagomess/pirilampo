package br.com.pirilampo.util;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class UiConsoleUtil {
    private static StringProperty logData = new SimpleStringProperty();

    public static StringProperty logDataProperty() {
        return logData;
    }

    public static void setLogData(String data) {
        try {
            Platform.runLater(() -> logData.set(data));
        }catch (Exception ignored){}
    }
}
