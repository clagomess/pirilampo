package br.com.pirilampo.controller;

import br.com.pirilampo.bean.MainForm;
import br.com.pirilampo.bean.Parametro;
import br.com.pirilampo.constant.Compilacao;
import br.com.pirilampo.core.Compilador;
import br.com.pirilampo.util.ExceptionUtil;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends MainForm implements Initializable {
    private final String MSG_OPE_SUCESSO = "Operação realizada com sucesso!";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //txtCorMenu.setText(Compilador.COR_MENU);
        //txtRootMenuNome.setText(Compilador.NOME_MENU_RAIZ);
    }

    public void selecionarFonte(){
        selecionarFonte(false);
    }

    public void selecionarFonteMaster(){
        selecionarFonte(true);
    }

    private void selecionarFonte(boolean isFonteMaster){
        File file;

        if(tipCompilacao.getSelectedToggle().getUserData() == Compilacao.FEATURE){
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Selecionar Fonte");

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Feature", "*.feature");
            chooser.getExtensionFilters().add(extFilter);

            file = chooser.showOpenDialog(new Stage());
        }else{
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Selecionar Pasta");
            file = directoryChooser.showDialog(new Stage());
        }

        if(file != null) {
            if(isFonteMaster){
                txtSrcFonteMaster.setText(file.getAbsolutePath());
            } else {
                txtSrcFonte.setText(file.getAbsolutePath());
            }
        }
    }

    public void compilarPdf(){
        compilar(true);
    }

    public void compilarHtml(){
        compilar(false);
    }

    private void compilar(boolean isPdf){
        if(StringUtils.isEmpty(txtSrcFonte.getText())){
            alertWarning(tipCompilacao.getSelectedToggle().getUserData() == Compilacao.FEATURE ? "Favor selecionar uma feature!" : "Favor selecionar uma pasta");
        }

        if(tipCompilacao.getSelectedToggle().getUserData() == Compilacao.DIFF && StringUtils.isEmpty(txtSrcFonteMaster.getText())){
            alertWarning("É necessário selecionar a pasta MASTER para realizar a comparação.");
        }

        Compilador compilador = new Compilador();
        Parametro parametro = new Parametro(this);

        new Thread(() -> {
            Platform.runLater(() -> progressBar.setProgress(-1));
            Platform.runLater(this::desabilitarBotoes);

            try {
                if(!isPdf && tipCompilacao.getSelectedToggle().getUserData() == Compilacao.FEATURE){
                    compilador.compilarFeature(parametro);
                }

                if(!isPdf && (tipCompilacao.getSelectedToggle().getUserData() == Compilacao.PASTA || tipCompilacao.getSelectedToggle().getUserData() == Compilacao.DIFF)){
                    compilador.compilarPasta(parametro);
                }

                if(isPdf && tipCompilacao.getSelectedToggle().getUserData() == Compilacao.FEATURE){
                    compilador.compilarFeaturePdf(parametro);
                }

                if(isPdf && tipCompilacao.getSelectedToggle().getUserData() == Compilacao.PASTA){
                    compilador.compilarPastaPdf(parametro);
                }

                Platform.runLater(() -> alertInfo(MSG_OPE_SUCESSO));
            } catch (Exception e) {
                Platform.runLater(() -> ExceptionUtil.showDialog(e));
            } finally {
                Platform.runLater(() -> progressBar.setProgress(0));
                Platform.runLater(this::habilitarBotoes);
            }
        }).start();
    }


    public void selecionarLogoSrc(){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Selecionar Imagens");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Imagens",
                "*.jpg", "*.jpeg", "*.png"
        );
        chooser.getExtensionFilters().add(extFilter);

        File file = chooser.showOpenDialog(new Stage());

        if(file != null) {
            txtLogoSrc.setText(file.getAbsolutePath());
        }
    }

    private void alertWarning(String msg){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(msg);

        alert.showAndWait();
    }

    private void alertInfo(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);

        alert.showAndWait();
    }

    private void desabilitarBotoes(){
        txtNome.setDisable(true);
        txtVersao.setDisable(true);
        txtLogoSrc.setDisable(true);
        btnSelecionarLogoSrc.setDisable(true);
        clrMenu.setDisable(true);
        clrTextoMenu.setDisable(true);
        txtNomeMenuRaiz.setDisable(true);
        sitEmbedarImagens.setDisable(true);
        txtSrcFonte.setDisable(true);
        txtSrcFonteMaster.setDisable(true);
        btnSelecionarFonte.setDisable(true);
        btnSelecionarFonteMaster.setDisable(true);
        btnGerarHtml.setDisable(true);
        btnGerarPdf.setDisable(true);
    }

    private void habilitarBotoes(){
        txtNome.setDisable(false);
        txtVersao.setDisable(false);
        txtLogoSrc.setDisable(false);
        btnSelecionarLogoSrc.setDisable(false);
        clrMenu.setDisable(false);
        clrTextoMenu.setDisable(false);
        txtNomeMenuRaiz.setDisable(false);
        sitEmbedarImagens.setDisable(false);
        txtSrcFonte.setDisable(false);
        txtSrcFonteMaster.setDisable(false);
        btnSelecionarFonte.setDisable(false);
        btnSelecionarFonteMaster.setDisable(false);
        btnGerarHtml.setDisable(false);
        btnGerarPdf.setDisable(false);
    }
}
