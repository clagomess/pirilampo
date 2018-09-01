package br.com.pirilampo.bean;

import br.com.pirilampo.constant.Compilacao;
import br.com.pirilampo.constant.LayoutPdf;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang.StringUtils;

@NoArgsConstructor
@Data
public class Parametro {
    private String txtNome = "Pirilampo";
    private String txtVersao = "1.0";
    private String txtLogoSrc;
    private LayoutPdf tipLayoutPdf = LayoutPdf.RETRATO;
    private String clrMenu = "#14171A";
    private String clrTextoMenu;
    private String txtNomeMenuRaiz = "Features";
    private boolean sitEmbedarImagens = true;
    private Compilacao tipCompilacao = Compilacao.PASTA;
    private String txtSrcFonte;
    private String txtSrcFonteMaster;
    private String txtOutputTarget;

    public Parametro(MainForm form){
        this.txtNome = form.txtNome.getText();
        this.txtVersao = form.txtVersao.getText();
        this.txtLogoSrc = form.txtLogoSrc.getText();
        this.tipLayoutPdf = (LayoutPdf) form.tipLayoutPdf.getSelectedToggle().getUserData();
        this.clrMenu = null; //@TODO: Implements
        this.clrTextoMenu = null; //@TODO: Implements
        this.txtNomeMenuRaiz = form.txtNomeMenuRaiz.getText();
        this.sitEmbedarImagens = form.sitEmbedarImagens.isSelected();
        this.tipCompilacao = (Compilacao) form.tipCompilacao.getSelectedToggle().getUserData();
        this.txtSrcFonte = form.txtSrcFonte.getText();
        this.txtSrcFonteMaster = form.txtSrcFonteMaster.getText();
    }

    public Parametro(CommandLine cmd){
        this.txtNome = cmd.getOptionValue("name");
        this.txtVersao = cmd.getOptionValue("version");
        this.txtSrcFonte = !StringUtils.isEmpty(cmd.getOptionValue("feature")) ? cmd.getOptionValue("feature") : this.txtSrcFonte;
        this.txtSrcFonte = !StringUtils.isEmpty(cmd.getOptionValue("feature_path")) ? cmd.getOptionValue("feature_path") : this.txtSrcFonte;
        this.txtSrcFonteMaster = cmd.getOptionValue("feature_path_master");
        this.txtOutputTarget = cmd.getOptionValue("output");
    }
}
