package com.github.clagomess.pirilampo.core.dto;

import com.github.clagomess.pirilampo.core.enums.CompilationArctifactEnum;
import com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum;
import com.github.clagomess.pirilampo.core.enums.HtmlPanelToggleEnum;
import com.github.clagomess.pirilampo.core.enums.LayoutPdfEnum;
import javafx.scene.paint.Color;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.Properties;

@Data
@NoArgsConstructor
public class ParametersDto {
    private String projectName = "Pirilampo";
    private String projectVersion = "1.0";
    private File projectLogo;
    private LayoutPdfEnum layoutPdf = LayoutPdfEnum.PORTRAIT;
    private HtmlPanelToggleEnum htmlPanelToggle = HtmlPanelToggleEnum.OPEN;
    private String menuColor = "#14171A";
    private String menuTextColor = "#DDDDDD";
    private Boolean embedImages = true;
    private CompilationTypeEnum compilationType = CompilationTypeEnum.FOLDER;
    private CompilationArctifactEnum compilationArctifact = CompilationArctifactEnum.HTML;
    private File projectSource;
    private File projectMasterSource;
    private File projectTarget;

    // public ParametroDto(MainForm form){
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
    // }

    /*
    public ParametersDto(CommandLine cmd){
        this.txtNome = cmd.getOptionValue("name");
        this.txtVersao = cmd.getOptionValue("version");
        this.txtSrcFonte = !StringUtils.isEmpty(cmd.getOptionValue("feature")) ? cmd.getOptionValue("feature") : this.txtSrcFonte;
        this.txtSrcFonte = !StringUtils.isEmpty(cmd.getOptionValue("feature_path")) ? cmd.getOptionValue("feature_path") : this.txtSrcFonte;
        this.txtSrcFonteMaster = cmd.getOptionValue("feature_path_master");
        this.txtOutputTarget = cmd.getOptionValue("output");
    }
    */

    public ParametersDto(Properties properties){
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
