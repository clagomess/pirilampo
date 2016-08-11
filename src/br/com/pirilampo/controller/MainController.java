package br.com.pirilampo.controller;

import br.com.pirilampo.util.Compilador;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MainController extends MainForm {
    public void selecionarFeature(){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Selecionar Feature");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Feature", "*.feature");
        chooser.getExtensionFilters().add(extFilter);

        File file = chooser.showOpenDialog(new Stage());

        if(file != null) {
            txtFeatureSrc.setText(file.getAbsolutePath());
        }
    }

    public void featureGerarHtml(){

    }

    public void featureGerarPdf(){

    }

    public void selecionarPasta(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Selecionar Pasta");

        File directory = directoryChooser.showDialog(new Stage());

        if(directory != null) {
            txtPastaSrc.setText(directory.getAbsolutePath());
        }
    }

    public void pastaGerarHtml(){
        Compilador compilador = new Compilador();

        if(txtPastaSrc.getText() != null && !txtPastaSrc.getText().trim().equals("")) {
            compilador.compilarPasta(txtPastaSrc.getText());
        }else{
            alertWarning(null, "É necessário selecionar uma pasta!");
        }
    }

    public void pastaGerarPdf(){

    }

    private void alertWarning(String title, String msg){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(title);
        alert.setContentText(msg);

        alert.showAndWait();
    }
}
