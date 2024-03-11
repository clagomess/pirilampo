package com.github.clagomess.pirilampo.gui.ui;

import com.github.clagomess.pirilampo.gui.form.MainForm;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class MainUI extends JFrame {
    private final MainForm form = new MainForm();

    public MainUI() {
        setTitle("Pirilampo");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(500, 400));
        setLayout(new MigLayout("insets 10 10 10 10", "[grow,fill]", ""));

        JPanel pSource = new JPanel(new MigLayout("", "[grow,fill]"));
        pSource.setBorder(BorderFactory.createTitledBorder("Source"));
        pSource.add(form.rbCompilationType, "wrap");
        pSource.add(form.fcProjectSource, "wrap");
        pSource.add(form.fcProjectMasterSource, "wrap");
        add(pSource, "wrap");

        JPanel pProject = new JPanel(new MigLayout("", "[grow,fill]"));
        pProject.setBorder(BorderFactory.createTitledBorder("Project"));
        pProject.add(new JLabel("Name"));
        pProject.add(new JLabel("Version"), "wrap");
        pProject.add(form.txtProjectName);
        pProject.add(form.txtProjectVersion, "wrap");
        pProject.add(new JLabel("Logo"), "wrap");
        pProject.add(form.fcProjectLogo, "span 2, wrap");
        add(pProject, "wrap");

        JTabbedPane pTabArtifact = new JTabbedPane();
        pTabArtifact.setBorder(BorderFactory.createTitledBorder("Artifact"));

        JPanel pHtml = new JPanel(new MigLayout("", "[grow,fill]"));
        pHtml.add(new JLabel("Panel Toggle"));
        pHtml.add(new JLabel("Embed Images?"), "wrap");
        pHtml.add(form.rbHtmlPanelToggle);
        pHtml.add(form.chkEmbedImages, "wrap");
        pHtml.add(new JLabel("Menu Color"));
        pHtml.add(new JLabel("Menu Text Color"), "wrap");
        pHtml.add(form.ccMenuColor);
        pHtml.add(form.ccMenuTextColor, "wrap");

        JPanel pPdf = new JPanel(new MigLayout("", "[grow,fill]"));
        pPdf.add(new JLabel("PDF Layout"), "wrap");
        pPdf.add(form.rbLayoutPdfEnum, "wrap");

        pTabArtifact.addTab("HTML", pHtml);
        pTabArtifact.addTab("PDF", pPdf);
        add(pTabArtifact, "wrap");

        /*
        JPanel pProgress = new JPanel(new MigLayout("", "[grow,fill]"));
        pProgress.setBorder(BorderFactory.createTitledBorder("Progress"));
        pProgress.add(new JProgressBar(), "wrap");
        pProgress.add(new JTextArea(), "wrap");
        add(pProgress);
        */

        add(form.btnCompile);
        getRootPane().setDefaultButton(form.btnCompile);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /*
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

        Preferences prefs = Preferences.userRoot().node(getClass().getName());

        String folder = prefs.get("LAST_USED_FOLDER", "");

        if(compilacao == Compilacao.FEATURE){
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Selecionar Fonte");

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Feature", "*.feature");
            chooser.getExtensionFilters().add(extFilter);

            if(folder != "") {
                File folderFile = new File(folder);
                if(folderFile.exists()) {
                    chooser.setInitialDirectory(new File(folder));
                }
            }

            file = chooser.showOpenDialog(new Stage());
        }else{
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Selecionar Pasta");

            if(folder != "") {
                File folderFile = new File(folder);
                if(folderFile.exists()) {
                    directoryChooser.setInitialDirectory(new File(folder));
                }
            }

            file = directoryChooser.showDialog(new Stage());
        }

        if(file != null) {
            prefs.put("LAST_USED_FOLDER", file.getAbsolutePath());
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
     */
}
