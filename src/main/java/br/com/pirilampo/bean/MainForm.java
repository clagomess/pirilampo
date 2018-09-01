package br.com.pirilampo.bean;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainForm {
    @FXML protected TextField txtNome;
    @FXML protected TextField txtVersao;
    @FXML protected TextField txtLogoSrc;
    @FXML protected Button btnSelecionarLogoSrc;
    @FXML protected ToggleGroup tipLayoutPdf;
    @FXML protected ColorPicker clrMenu;
    @FXML protected ColorPicker clrTextoMenu;
    @FXML protected TextField txtNomeMenuRaiz;
    @FXML protected CheckBox sitEmbedarImagens;
    @FXML protected ToggleGroup tipCompilacao;
    @FXML protected TextField txtSrcFonte;
    @FXML protected TextField txtSrcFonteMaster;
    @FXML protected Button btnSelecionarFonte;
    @FXML protected Button btnSelecionarFonteMaster;
    @FXML protected Button btnGerarHtml;
    @FXML protected Button btnGerarPdf;
    @FXML protected ProgressBar progressBar;
}
