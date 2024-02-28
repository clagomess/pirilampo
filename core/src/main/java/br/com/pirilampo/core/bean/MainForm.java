package br.com.pirilampo.core.bean;

import br.com.pirilampo.core.dto.ParametroDto;
import br.com.pirilampo.core.enums.PainelEnum;
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
    @FXML protected ToggleGroup tipPainelFechado;
    @FXML protected ColorPicker clrMenu;
    @FXML protected ColorPicker clrTextoMenu;
    @FXML protected CheckBox sitEmbedarImagens;
    @FXML protected ToggleGroup tipCompilacao;
    @FXML protected TextField txtSrcFonte;
    @FXML protected TextField txtSrcFonteMaster;
    @FXML protected Button btnSelecionarFonte;
    @FXML protected Button btnSelecionarFonteMaster;
    @FXML protected Button btnGerarHtml;
    @FXML protected Button btnGerarPdf;
    @FXML protected ProgressBar progressBar;
    @FXML protected TextArea txtConsole;

    protected void setData(ParametroDto parametro){
        this.txtNome.setText(parametro.getTxtNome());
        this.txtVersao.setText(parametro.getTxtVersao());
        this.txtLogoSrc.setText(parametro.getTxtLogoSrc());
        this.clrMenu.setValue(Color.web(parametro.getClrMenu()));
        this.clrTextoMenu.setValue(Color.web(parametro.getClrTextoMenu()));
        this.sitEmbedarImagens.setSelected(parametro.getSitEmbedarImagens());

        if(parametro.getTipPainel() != null) {
            this.tipPainelFechado.getToggles().forEach(toggle -> {
                if(PainelEnum.valueOf(toggle.getUserData().toString()).equals(parametro.getTipPainel())) {
                    toggle.setSelected(true);
                }
            });
        }
    }
}
