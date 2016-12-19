package br.com.pirilampo.controller;

import br.com.pirilampo.util.Compilador;
import br.com.pirilampo.util.ExceptionUtil;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MainController extends MainForm {
    private final String MSG_SELECIONAR_PASTA = "É necessário selecionar uma pasta!";
    private final String MSG_SELECIONAR_FEATURE = "É necessário selecionar uma feature!";
    private final String MSG_OPE_SUCESSO = "Operação realizada com sucesso!";

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
        Compilador compilador = new Compilador();

        if (txtFeatureSrc.getText() != null && !txtFeatureSrc.getText().trim().equals("")) {
            new Thread(() -> {
                Platform.runLater(() -> progressBar.setProgress(-1));
                Platform.runLater(this::desabilitarBotoes);

                try {
                    compilador.compilarFeature(txtFeatureSrc.getText(), txtNome.getText(), txtVersao.getText());

                    Platform.runLater(() -> alertInfo(null, MSG_OPE_SUCESSO));
                } catch (Exception e) {
                    Platform.runLater(() -> ExceptionUtil.showDialog(e));
                }

                Platform.runLater(() -> progressBar.setProgress(0));
                Platform.runLater(this::habilitarBotoes);
            }).start();
        } else {
            alertWarning(null, MSG_SELECIONAR_FEATURE);
        }
    }

    public void featureGerarPdf(){
        Compilador compilador = new Compilador();

        if(txtFeatureSrc.getText() != null && !txtFeatureSrc.getText().trim().equals("")) {
            new Thread(() -> {
                Platform.runLater(() -> progressBar.setProgress(-1));
                Platform.runLater(this::desabilitarBotoes);

                try {
                    compilador.compilarFeaturePdf(
                            txtFeatureSrc.getText(),
                            txtNome.getText(),
                            txtVersao.getText(),
                            (String) rdoLayoutPdf.getSelectedToggle().getUserData()
                    );

                    Platform.runLater(() -> alertInfo(null, MSG_OPE_SUCESSO));
                } catch (Exception e) {
                    Platform.runLater(() -> ExceptionUtil.showDialog(e));
                }

                Platform.runLater(() -> progressBar.setProgress(0));
                Platform.runLater(this::habilitarBotoes);
            }).start();
        }else{
            alertWarning(null, MSG_SELECIONAR_FEATURE);
        }
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
            new Thread(() -> {
                Platform.runLater(() -> progressBar.setProgress(-1));
                Platform.runLater(this::desabilitarBotoes);

                try {
                    compilador.compilarPasta(txtPastaSrc.getText(), null, txtNome.getText(), txtVersao.getText());

                    Platform.runLater(() -> alertInfo(null, MSG_OPE_SUCESSO));
                } catch (Exception e) {
                    Platform.runLater(() -> ExceptionUtil.showDialog(e));
                }

                Platform.runLater(() -> progressBar.setProgress(0));
                Platform.runLater(this::habilitarBotoes);
            }).start();
        }else{
            alertWarning(null, MSG_SELECIONAR_PASTA);
        }
    }

    public void pastaGerarPdf(){
        Compilador compilador = new Compilador();

        if(txtPastaSrc.getText() != null && !txtPastaSrc.getText().trim().equals("")) {
            new Thread(() -> {
                Platform.runLater(() -> progressBar.setProgress(-1));
                Platform.runLater(this::desabilitarBotoes);

                try {
                    compilador.compilarPastaPdf(
                            txtPastaSrc.getText(),
                            txtNome.getText(),
                            txtVersao.getText(),
                            (String) rdoLayoutPdf.getSelectedToggle().getUserData()
                    );

                    Platform.runLater(() -> alertInfo(null, MSG_OPE_SUCESSO));
                } catch (Exception e) {
                    Platform.runLater(() -> ExceptionUtil.showDialog(e));
                }

                Platform.runLater(() -> progressBar.setProgress(0));
                Platform.runLater(this::habilitarBotoes);
            }).start();
        }else{
            alertWarning(null, MSG_SELECIONAR_PASTA);
        }
    }

    private void alertWarning(String title, String msg){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(title);
        alert.setContentText(msg);

        alert.showAndWait();
    }

    private void alertInfo(String title, String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.setContentText(msg);

        alert.showAndWait();
    }

    private void desabilitarBotoes(){
        btnSelecionarFeature.setDisable(true);
        btnFeatureGerarHtml.setDisable(true);
        btnFeatureGerarPdf.setDisable(true);
        btnSelecionarPasta.setDisable(true);
        btnPastaGerarHtml.setDisable(true);
        btnPastaGerarPdf.setDisable(true);
    }

    private void habilitarBotoes(){
        btnSelecionarFeature.setDisable(false);
        btnFeatureGerarHtml.setDisable(false);
        btnFeatureGerarPdf.setDisable(false);
        btnSelecionarPasta.setDisable(false);
        btnPastaGerarHtml.setDisable(false);
        btnPastaGerarPdf.setDisable(false);
    }
}
