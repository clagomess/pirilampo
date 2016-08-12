package br.com.pirilampo.util;


import javafx.scene.control.Alert;

public class ExceptionUtil {
    public static void showDialog(Exception e){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Erro ao executar a operação.");
        alert.setContentText(e.getMessage());
        alert.setResizable(true);

        alert.showAndWait();
    }
}
