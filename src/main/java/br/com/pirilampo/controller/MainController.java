package br.com.pirilampo.controller;

import br.com.pirilampo.bean.MainForm;
import br.com.pirilampo.bean.Parametro;
import br.com.pirilampo.bind.ConsoleBind;
import br.com.pirilampo.bind.ProgressBind;
import br.com.pirilampo.constant.Artefato;
import br.com.pirilampo.constant.Compilacao;
import br.com.pirilampo.core.Compilador;
import br.com.pirilampo.util.ExceptionUtil;
import br.com.pirilampo.util.PropertiesUtil;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
public class MainController extends MainForm implements Initializable {
    private final String MSG_OPE_SUCESSO = "Operação realizada com sucesso!";
    private final List<Color> cores = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Parametro parametro = new Parametro();

        cores.add(Color.web(parametro.getClrTextoMenu()));
        cores.add(Color.web(parametro.getClrMenu()));
        cores.add(Color.web("#D50000"));
        cores.add(Color.web("#E67C73"));
        cores.add(Color.web("#F4511E"));
        cores.add(Color.web("#F6BF26"));
        cores.add(Color.web("#33B679"));
        cores.add(Color.web("#0B8043"));
        cores.add(Color.web("#039BE5"));
        cores.add(Color.web("#3F51B5"));
        cores.add(Color.web("#7986CB"));
        cores.add(Color.web("#8E24AA"));
        cores.add(Color.web("#616161"));
        cores.add(Color.web("#FFFFFF"));
        cores.add(Color.web("#040404"));

        for(Color cor : cores){
            clrMenu.getCustomColors().add(cor);
            clrTextoMenu.getCustomColors().add(cor);
        }

        clrMenu.setValue(Color.web(parametro.getClrMenu()));
        clrTextoMenu.setValue(Color.web(parametro.getClrTextoMenu()));

        txtConsole.textProperty().bind(ConsoleBind.getLogData());
        progressBar.progressProperty().bind(ProgressBind.getProgress());
    }

    public void selecionarFonte(){
        selecionarFonte(false);
    }

    public void selecionarFonteMaster(){
        selecionarFonte(true);
    }

    private void selecionarFonte(boolean isFonteMaster){
        File file;
        final Compilacao compilacao = Compilacao.valueOf((String) tipCompilacao.getSelectedToggle().getUserData());

        if(compilacao == Compilacao.FEATURE){
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
            if(!isFonteMaster && compilacao != Compilacao.FEATURE){
                setData(PropertiesUtil.getData(file.getAbsolutePath()));
            }

            if(isFonteMaster){
                txtSrcFonteMaster.setText(file.getAbsolutePath());

                if(txtSrcFonte.getText().equals(txtSrcFonteMaster.getText())){
                    showDialog(Alert.AlertType.WARNING, "As pastas para comparação não podem ser iguais.");
                    txtSrcFonte.setText("");
                    txtSrcFonteMaster.setText("");
                }
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
        final Compilacao compilacao = Compilacao.valueOf((String) tipCompilacao.getSelectedToggle().getUserData());

        if(StringUtils.isEmpty(txtSrcFonte.getText())){
            showDialog(Alert.AlertType.WARNING, compilacao == Compilacao.FEATURE ? "Favor selecionar uma feature!" : "Favor selecionar uma pasta");
            return;
        }

        if(compilacao == Compilacao.DIFF && StringUtils.isEmpty(txtSrcFonteMaster.getText())){
            showDialog(Alert.AlertType.WARNING, "É necessário selecionar a pasta MASTER para realizar a comparação.");
            return;
        }

        Compilador compilador = new Compilador();
        Parametro parametro = new Parametro(this);
        parametro.setArtefato(isPdf ? Artefato.PDF : Artefato.HTML);

        new Thread(() -> {
            ProgressBind.setProgress(-1);
            Platform.runLater(() -> root.setDisable(true));

            try {
                if(!isPdf && compilacao == Compilacao.FEATURE){
                    compilador.compilarFeature(parametro);
                }

                if(!isPdf && (compilacao == Compilacao.PASTA || compilacao == Compilacao.DIFF)){
                    compilador.compilarPasta(parametro);
                    PropertiesUtil.setData(parametro);
                }

                if(isPdf && compilacao == Compilacao.FEATURE){
                    compilador.compilarFeaturePdf(parametro);
                }

                if(isPdf && compilacao == Compilacao.PASTA){
                    compilador.compilarPastaPdf(parametro);
                }

                Platform.runLater(() -> showDialog(Alert.AlertType.INFORMATION, MSG_OPE_SUCESSO));
                log.info(MSG_OPE_SUCESSO);
            } catch (Throwable e) {
                Platform.runLater(() -> ExceptionUtil.showDialog(e));
            } finally {
                ProgressBind.setProgress(0);
                Platform.runLater(() -> root.setDisable(false));
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

    public void tipCompilacaoChange(){
        final Compilacao compilacao = Compilacao.valueOf((String) tipCompilacao.getSelectedToggle().getUserData());

        txtSrcFonte.setText("");
        txtSrcFonteMaster.setText("");
        txtSrcFonteMaster.setDisable(compilacao != Compilacao.DIFF);
        btnSelecionarFonteMaster.setDisable(compilacao != Compilacao.DIFF);
    }

    private void showDialog(Alert.AlertType alertType, String msg){
        Alert alert = new Alert(alertType);
        alert.setHeaderText(msg);

        alert.showAndWait();
    }
}
