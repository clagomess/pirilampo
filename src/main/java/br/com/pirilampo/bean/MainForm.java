package br.com.pirilampo.bean;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class MainForm {
    @FXML protected GridPane root;
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

    protected void setData(Parametro parametro){
        this.txtNome.setText(parametro.getTxtNome());
        this.txtVersao.setText(parametro.getTxtVersao());
        this.txtLogoSrc.setText(parametro.getTxtLogoSrc());
        this.clrMenu.setValue(Color.web(parametro.getClrMenu()));
        this.clrTextoMenu.setValue(Color.web(parametro.getClrTextoMenu()));
        this.txtNomeMenuRaiz.setText(parametro.getTxtNomeMenuRaiz());
        this.sitEmbedarImagens.setSelected(parametro.getSitEmbedarImagens());
    }
}
