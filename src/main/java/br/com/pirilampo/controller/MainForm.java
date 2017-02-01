package br.com.pirilampo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

class MainForm {
    @FXML TextField txtNome;
    @FXML TextField txtVersao;
    @FXML TextField txtFeatureSrc;
    @FXML TextField txtPastaSrc;
    @FXML ProgressBar progressBar;
    @FXML Button btnSelecionarFeature;
    @FXML Button btnFeatureGerarHtml;
    @FXML Button btnFeatureGerarPdf;
    @FXML Button btnSelecionarPasta;
    @FXML Button btnPastaGerarHtml;
    @FXML Button btnPastaGerarPdf;
    @FXML ToggleGroup rdoLayoutPdf;
    @FXML TextField txtCorMenu;
    @FXML TextField txtRootMenuNome;
    @FXML TextField txtBrandSrc;
    @FXML Button btnBrandSrc;
}
