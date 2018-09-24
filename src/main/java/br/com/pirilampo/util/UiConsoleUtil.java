package br.com.pirilampo.util;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

public final class UiConsoleUtil {
    @Getter
    private static StringProperty logData = new SimpleStringProperty();

    static void setLogData(String data) {
        try {
            Platform.runLater(() -> logData.set(data));
        }catch (Exception ignored){}
    }
}
