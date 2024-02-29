package br.com.pirilampo.core.dto;

import br.com.pirilampo.core.bean.MainForm;
import br.com.pirilampo.core.enums.ArtefatoEnum;
import br.com.pirilampo.core.enums.CompilacaoEnum;
import br.com.pirilampo.core.enums.LayoutPdfEnum;
import br.com.pirilampo.core.enums.PainelEnum;
import javafx.scene.paint.Color;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.Properties;

@NoArgsConstructor
@Data
public class ParametroDto {
    private String txtNome = "Pirilampo";
    private String txtVersao = "1.0";
    private File txtLogoSrc;
    private LayoutPdfEnum tipLayoutPdf = LayoutPdfEnum.RETRATO;
    private PainelEnum tipPainel = PainelEnum.ABERTO;
    private String clrMenu = "#14171A";
    private String clrTextoMenu = "#DDDDDD";
    private Boolean sitEmbedarImagens = true;
    private CompilacaoEnum tipCompilacao = CompilacaoEnum.PASTA;
    private ArtefatoEnum artefato = ArtefatoEnum.HTML;
    private File txtSrcFonte;
    private File txtSrcFonteMaster;
    private File txtOutputTarget;

    public ParametroDto(MainForm form){
        /*
        this.txtNome = !StringUtils.isEmpty(form.txtNome.getText()) ? form.txtNome.getText() : this.txtNome;
        this.txtVersao = !StringUtils.isEmpty(form.txtVersao.getText()) ? form.txtVersao.getText() : this.txtVersao;
        this.txtLogoSrc = form.txtLogoSrc.getText();
        this.tipLayoutPdf = LayoutPdfEnum.valueOf((String) form.tipLayoutPdf.getSelectedToggle().getUserData());
        this.tipPainelFechado = PainelFechadoEnum.valueOf((String) form.tipPainelFechado.getSelectedToggle().getUserData());
        this.clrMenu = colorHex(form.clrMenu.getValue());
        this.clrTextoMenu = colorHex(form.clrTextoMenu.getValue());
        this.sitEmbedarImagens = form.sitEmbedarImagens.isSelected();
        this.tipCompilacao = CompilacaoEnum.valueOf((String) form.tipCompilacao.getSelectedToggle().getUserData());
        this.txtSrcFonte = form.txtSrcFonte.getText();
        this.txtSrcFonteMaster = form.txtSrcFonteMaster.getText();
         */
    }

    public ParametroDto(CommandLine cmd){
        /*
        this.txtNome = cmd.getOptionValue("name");
        this.txtVersao = cmd.getOptionValue("version");
        this.txtSrcFonte = !StringUtils.isEmpty(cmd.getOptionValue("feature")) ? cmd.getOptionValue("feature") : this.txtSrcFonte;
        this.txtSrcFonte = !StringUtils.isEmpty(cmd.getOptionValue("feature_path")) ? cmd.getOptionValue("feature_path") : this.txtSrcFonte;
        this.txtSrcFonteMaster = cmd.getOptionValue("feature_path_master");
        this.txtOutputTarget = cmd.getOptionValue("output");
        */
    }

    public ParametroDto(Properties properties){
        /*
        this.txtNome = !StringUtils.isEmpty(properties.getProperty("txtNome")) ? properties.getProperty("txtNome") : this.txtNome;
        this.txtVersao = !StringUtils.isEmpty(properties.getProperty("txtVersao")) ? properties.getProperty("txtVersao") : this.txtVersao;
        this.txtLogoSrc = !StringUtils.isEmpty(properties.getProperty("txtLogoSrc")) ? properties.getProperty("txtLogoSrc") : this.txtLogoSrc;
        this.clrMenu = !StringUtils.isEmpty(properties.getProperty("clrMenu")) ? properties.getProperty("clrMenu") : this.clrMenu;
        this.clrTextoMenu = !StringUtils.isEmpty(properties.getProperty("clrTextoMenu")) ? properties.getProperty("clrTextoMenu") : this.clrTextoMenu;
        this.sitEmbedarImagens = !StringUtils.isEmpty(properties.getProperty("sitEmbedarImagens")) ? Boolean.valueOf(properties.getProperty("sitEmbedarImagens")) : this.sitEmbedarImagens;
        this.tipPainel = !StringUtils.isEmpty(properties.getProperty("tipPainelFechado")) ? PainelEnum.valueOf(properties.getProperty("tipPainelFechado")) : this.tipPainel;
         */
    }

    public String colorHex(Color color){
        return '#' + color.toString().substring(2, 8);
    }
}
