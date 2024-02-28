package br.com.pirilampo.bind;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import lombok.Getter;

public final class ProgressBind {
    @Getter
    private static DoubleProperty progress = new SimpleDoubleProperty();

    public static void setProgress(double data) {
        try {
            Platform.runLater(() -> progress.set(data));
        }catch (Exception ignored){}
    }
}
