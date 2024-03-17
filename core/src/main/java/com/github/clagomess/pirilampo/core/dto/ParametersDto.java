package com.github.clagomess.pirilampo.core.dto;

import com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum;
import com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum;
import com.github.clagomess.pirilampo.core.enums.HtmlPanelToggleEnum;
import com.github.clagomess.pirilampo.core.enums.LayoutPdfEnum;
import com.github.clagomess.pirilampo.core.exception.ParametersException;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

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
        if(StringUtils.isBlank(projectVersion)) throw ParametersException.required("Project Version");

        if(projectLogo != null && !projectLogo.isFile()){ //@TODO: can be a project logo
            throw new ParametersException("Option <Project Logo> must be a valid file");
        }

        if(layoutPdf == null) throw ParametersException.required("Layout PDF");
        if(htmlPanelToggle == null) throw ParametersException.required("HTML Panel Toggle");

        if(menuColor == null) throw ParametersException.required("Menu Color");
        if(!Pattern.compile("^#[A-Fa-f0-9]{6}$").matcher(menuColor).find()){
            throw new ParametersException("Option <Menu Color> must be a valid hex color");
        }

        if(menuTextColor == null) throw ParametersException.required("Menu Text Color");
        if(!Pattern.compile("^#[A-Fa-f0-9]{6}$").matcher(menuTextColor).find()){
            throw new ParametersException("Option <Menu Text Color> must be a valid hex color");
        }

        if(compilationType == null) throw ParametersException.required("Compilation Type");
        if(compilationArtifact == null) throw ParametersException.required("Compilation Artifact");

        if(projectSource == null || !projectSource.exists()){
            throw new ParametersException("Option <Project Source> must be valid");
        }

        if(projectSource.isFile() &&
                Arrays.asList(
                        CompilationTypeEnum.FOLDER,
                        CompilationTypeEnum.FOLDER_DIFF
                ).contains(compilationType)
        ){
            throw new ParametersException("Option <Project Source> must be a folder");
        }

        if(projectSource.isDirectory() && compilationType == CompilationTypeEnum.FEATURE){
            throw new ParametersException("Option <Project Source> must be a *.feature file");
        }

        if(projectMasterSource == null && compilationType == CompilationTypeEnum.FOLDER_DIFF){
            throw ParametersException.required("Project Master Source");
        }

        if(projectMasterSource != null && compilationType != CompilationTypeEnum.FOLDER_DIFF){
            throw new ParametersException("Option <Project Master Source> must be none");
        }

        if(projectMasterSource != null && !projectMasterSource.isDirectory()){
            throw new ParametersException("Option <Project Master Source> must be a folder");
        }

        if(Objects.equals(projectMasterSource, projectSource)){
            throw new ParametersException("Option <Project Master Source> can't be equals <Project Source>");
        }

        if(projectTarget != null && !projectTarget.isDirectory()){
            throw new ParametersException("Option <Project Target> must be a folder");
        }
    }
}
