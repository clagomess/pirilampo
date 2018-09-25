package br.com.pirilampo.bind;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

public final class ConsoleBind {
    @Getter
    private static StringProperty logData = new SimpleStringProperty();

    public static void setLogData(String data) {
        try {
            Platform.runLater(() -> logData.set(data));
        }catch (Exception ignored){}
    }
}
