package br.com.pirilampo.util;


import javafx.scene.control.Alert;

public class ExceptionUtil {
    public static void showDialog(Exception e){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(e.getMessage());

        String trace = "";
        if(e.getStackTrace().length > 0){
            for (StackTraceElement traceItem : e.getStackTrace()) {
                trace += traceItem.toString() + "\n";
            }
        }

        alert.setContentText(trace);

        alert.showAndWait();
    }
}
