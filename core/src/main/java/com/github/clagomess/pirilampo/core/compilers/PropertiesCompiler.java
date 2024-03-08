package com.github.clagomess.pirilampo.core.compilers;

import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum;
import com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum;
import com.github.clagomess.pirilampo.core.enums.HtmlPanelToggleEnum;
import com.github.clagomess.pirilampo.core.enums.LayoutPdfEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Properties;

@Slf4j
public class PropertiesCompiler extends Compiler {
    protected static final String FILENAME = "config.properties";

    protected File getSourceDir(ParametersDto parameters){
        if(parameters.getProjectSource().isDirectory()){
            return parameters.getProjectSource();
        } else {
            return parameters.getProjectSource().getParentFile();
        }
    }

    public void loadData(ParametersDto parameters){
        File file = new File(getSourceDir(parameters), FILENAME);
        if(!file.isFile()) return;

        try (InputStream input = Files.newInputStream(file.toPath())){
            Properties prop = new Properties();
            prop.load(input);

            parameters.setProjectName(prop.getProperty("projectName", parameters.getProjectName()));
            parameters.setProjectVersion(prop.getProperty("projectVersion", parameters.getProjectVersion()));

            if(prop.containsKey("projectLogo")){
                parameters.setProjectLogo(StringUtils.isNotBlank(prop.getProperty("projectLogo")) ?
                        new File(prop.getProperty("projectLogo")) :
                        null
                );
            }

            parameters.setLayoutPdf(LayoutPdfEnum.valueOf(prop.getProperty(
                    "layoutPdf",
                    parameters.getLayoutPdf().name()
            )));

            parameters.setHtmlPanelToggle(HtmlPanelToggleEnum.valueOf(prop.getProperty(
                    "htmlPanelToggle",
                    parameters.getHtmlPanelToggle().name()
            )));

            parameters.setMenuColor(prop.getProperty("menuColor", parameters.getMenuColor()));
            parameters.setMenuTextColor(prop.getProperty("menuTextColor", parameters.getMenuTextColor()));

            parameters.setEmbedImages(Boolean.parseBoolean(prop.getProperty(
                    "embedImages",
                    String.valueOf(parameters.isEmbedImages())
            )));

            parameters.setCompilationType(CompilationTypeEnum.valueOf(prop.getProperty(
                    "compilationType",
                    parameters.getCompilationType().name()
            )));

            parameters.setCompilationArtifact(CompilationArtifactEnum.valueOf(prop.getProperty(
                    "compilationArtifact",
                    parameters.getCompilationArtifact().name()
            )));

        } catch (IOException e) {
            log.error(log.getName(), e);
        }
    }

    public void setData(ParametersDto parameters){
        File file = new File(getSourceDir(parameters), FILENAME);

        try (OutputStream output = Files.newOutputStream(file.toPath())){
            Properties prop = new Properties();
            prop.setProperty("projectName", parameters.getProjectName());
            prop.setProperty("projectVersion", parameters.getProjectVersion());
            prop.setProperty("projectLogo",
                    parameters.getProjectLogo() != null ?
                            parameters.getProjectLogo().getAbsolutePath() :
                            ""
            );
            prop.setProperty("layoutPdf", parameters.getLayoutPdf().name());
            prop.setProperty("htmlPanelToggle", parameters.getHtmlPanelToggle().name());
            prop.setProperty("menuColor", parameters.getMenuColor());
            prop.setProperty("menuTextColor", parameters.getMenuTextColor());
            prop.setProperty("embedImages", String.valueOf(parameters.isEmbedImages()));
            prop.setProperty("compilationType", parameters.getCompilationType().name());
            prop.setProperty("compilationArtifact", parameters.getCompilationArtifact().name());

            prop.store(output, null);
        } catch (IOException ex) {
            log.error(log.getName(), ex);
        }
    }
}
