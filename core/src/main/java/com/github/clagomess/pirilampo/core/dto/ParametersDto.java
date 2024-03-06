package com.github.clagomess.pirilampo.core.dto;

import com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum;
import com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum;
import com.github.clagomess.pirilampo.core.enums.HtmlPanelToggleEnum;
import com.github.clagomess.pirilampo.core.enums.LayoutPdfEnum;
import com.github.clagomess.pirilampo.core.exception.ParametersException;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.io.File;

@Data
@NoArgsConstructor
public class ParametersDto {
    private String projectName = "Pirilampo";
    private String projectVersion = "1.0";
    private File projectLogo;
    private LayoutPdfEnum layoutPdf = LayoutPdfEnum.PORTRAIT;
    private HtmlPanelToggleEnum htmlPanelToggle = HtmlPanelToggleEnum.OPENED;
    private String menuColor = "#14171A";
    private String menuTextColor = "#DDDDDD";
    private boolean embedImages = true;
    private CompilationTypeEnum compilationType = CompilationTypeEnum.FOLDER;
    private CompilationArtifactEnum compilationArtifact = CompilationArtifactEnum.HTML;
    private File projectSource;
    private File projectMasterSource;
    private File projectTarget;

    public void validate() throws ParametersException {
        if(StringUtils.isBlank(projectName)) throw ParametersException.required("Project Name");
        if(StringUtils.isBlank(projectVersion)) throw ParametersException.required("Project Name");

        if(projectLogo != null && !projectLogo.isFile()){
            throw new ParametersException("Option <Project Logo> must be a valid file");
        }

        if(layoutPdf == null) throw ParametersException.required("Layout PDF");
        if(htmlPanelToggle == null) throw ParametersException.required("HTML Panel Toggle");

        //@TODO: menuColor
        //@TODO: menuTextColor

        if(compilationType == null) throw ParametersException.required("Compilation Type");
        if(compilationArtifact == null) throw ParametersException.required("Compilation Artifact");

        //@TODO: projectSource
        //@TODO: projectMasterSource
        //@TODO: projectTarget
    }
}
