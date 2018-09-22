package br.com.pirilampo.bean;

import br.com.pirilampo.constant.Compilacao;
import br.com.pirilampo.constant.LayoutPdf;
import javafx.scene.paint.Color;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang.StringUtils;

import java.util.Properties;

@NoArgsConstructor
@Data
public class Parametro {
    private String txtNome = "Pirilampo";
    private String txtVersao = "1.0";
    private String txtLogoSrc;
    private LayoutPdf tipLayoutPdf = LayoutPdf.RETRATO;
    private String clrMenu = "#14171A";
    private String clrTextoMenu = "#DDDDDD";
    private Boolean sitEmbedarImagens = true;
    private Compilacao tipCompilacao = Compilacao.PASTA;
    private String txtSrcFonte;
    private String txtSrcFonteMaster;
    private String txtOutputTarget;

    public Parametro(MainForm form){
        this.txtNome = !StringUtils.isEmpty(form.txtNome.getText()) ? form.txtNome.getText() : this.txtNome;
        this.txtVersao = !StringUtils.isEmpty(form.txtVersao.getText()) ? form.txtVersao.getText() : this.txtVersao;
        this.txtLogoSrc = form.txtLogoSrc.getText();
        this.tipLayoutPdf = LayoutPdf.valueOf((String) form.tipLayoutPdf.getSelectedToggle().getUserData());
        this.clrMenu = colorHex(form.clrMenu.getValue());
        this.clrTextoMenu = colorHex(form.clrTextoMenu.getValue());
        this.sitEmbedarImagens = form.sitEmbedarImagens.isSelected();
        this.tipCompilacao = Compilacao.valueOf((String) form.tipCompilacao.getSelectedToggle().getUserData());
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

    public Parametro(Properties properties){
        this.txtNome = !StringUtils.isEmpty(properties.getProperty("txtNome")) ? properties.getProperty("txtNome") : this.txtNome;
        this.txtVersao = !StringUtils.isEmpty(properties.getProperty("txtVersao")) ? properties.getProperty("txtVersao") : this.txtVersao;
        this.txtLogoSrc = !StringUtils.isEmpty(properties.getProperty("txtLogoSrc")) ? properties.getProperty("txtLogoSrc") : this.txtLogoSrc;
        this.clrMenu = !StringUtils.isEmpty(properties.getProperty("clrMenu")) ? properties.getProperty("clrMenu") : this.clrMenu;
        this.clrTextoMenu = !StringUtils.isEmpty(properties.getProperty("clrTextoMenu")) ? properties.getProperty("clrTextoMenu") : this.clrTextoMenu;
        this.sitEmbedarImagens = !StringUtils.isEmpty(properties.getProperty("sitEmbedarImagens")) ? Boolean.valueOf(properties.getProperty("sitEmbedarImagens")) : this.sitEmbedarImagens;
    }

    public String colorHex(Color color){
        return '#' + color.toString().substring(2, 8);
    }
}
