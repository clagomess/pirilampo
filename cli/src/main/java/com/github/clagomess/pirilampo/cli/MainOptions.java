package com.github.clagomess.pirilampo.cli;

import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum;
import com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum;
import com.github.clagomess.pirilampo.core.enums.HtmlPanelToggleEnum;
import com.github.clagomess.pirilampo.core.enums.LayoutPdfEnum;
import com.github.clagomess.pirilampo.core.exception.ParametersException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.Arrays;

@Slf4j
public class MainOptions {
    private final ParametersDto defaultParameters = new ParametersDto();

    private final Option projectName = Option.builder()
            .option("projectName")
            .desc(String.format(
                    "Project Name. Default: %s",
                    defaultParameters.getProjectName()
            ))
            .hasArg().build();

    private final Option projectVersion = Option.builder()
            .option("projectVersion")
            .desc(String.format(
                    "Project Version. Default: %s",
                    defaultParameters.getProjectVersion()
            ))
            .hasArg().build();

    private final Option projectLogo = Option.builder()
            .option("projectLogo")
            .desc("Image file for logo")
            .hasArg().build();

    private final Option layoutPdf = Option.builder()
            .option("layoutPdf")
            .desc(String.format(
                    "Layout PDF. Expected values: %s. Default: %s",
                    Arrays.toString(LayoutPdfEnum.values()),
                    defaultParameters.getLayoutPdf()
            ))
            .hasArg().build();

    private final Option htmlPanelToggle = Option.builder()
            .option("htmlPanelToggle")
            .desc(String.format(
                    "Panel Toggle. Expected values: %s. Default: %s",
                    Arrays.toString(HtmlPanelToggleEnum.values()),
                    defaultParameters.getHtmlPanelToggle()
            ))
            .hasArg().build();

    private final Option menuColor = Option.builder()
            .option("menuColor")
            .desc(String.format(
                    "Menu Color. Default: %s",
                    defaultParameters.getMenuColor()
            ))
            .hasArg().build();

    private final Option menuTextColor = Option.builder()
            .option("menuTextColor")
            .desc(String.format(
                    "Menu Text Color. Default: %s",
                    defaultParameters.getMenuTextColor()
            ))
            .hasArg().build();

    private final Option disableEmbedImages = Option.builder()
            .option("disableEmbedImages")
            .desc("Disable Emded Images?")
            .build();

    private final Option compilationType = Option.builder()
            .option("compilationType")
            .desc(String.format(
                    "Compilation Type. Expected values: %s. Default: %s",
                    Arrays.toString(CompilationTypeEnum.values()),
                    defaultParameters.getCompilationType()
            ))
            .hasArg().build();

    private final Option compilationArtifact = Option.builder()
            .option("compilationArtifact")
            .desc(String.format(
                    "Compilation Artifact. Expected values: %s. Default: %s",
                    Arrays.toString(CompilationArtifactEnum.values()),
                    defaultParameters.getCompilationArtifact()
            ))
            .hasArg().build();

    private final Option projectSource = Option.builder()
            .option("projectSource")
            .desc("Folder or *.feature")
            .hasArg().required().build();

    private final Option projectMasterSource = Option.builder()
            .option("projectMasterSource")
            .desc("Folder to compare")
            .hasArg().build();

    private final Option projectTarget = Option.builder()
            .option("projectTarget")
            .desc("Target Folder")
            .hasArg().build();

    private final Options options = new Options(){{
        addOption(projectName);
        addOption(projectVersion);
        addOption(projectLogo);
        addOption(layoutPdf);
        addOption(htmlPanelToggle);
        addOption(menuColor);
        addOption(menuTextColor);
        addOption(disableEmbedImages);
        addOption(compilationType);
        addOption(compilationArtifact);
        addOption(projectSource);
        addOption(projectMasterSource);
        addOption(projectTarget);
    }};

    public ParametersDto getArgs(String[] argv){
        try {
            CommandLine cmd = new DefaultParser().parse(options, argv);
            ParametersDto parameters = new ParametersDto();

            if(cmd.hasOption(projectName)) {
                parameters.setProjectName(cmd.getOptionValue(projectName));
            }

            if(cmd.hasOption(projectVersion)) {
                parameters.setProjectVersion(cmd.getOptionValue(projectVersion));
            }

            if(cmd.hasOption(projectLogo)) {
                parameters.setProjectLogo(new File(cmd.getOptionValue(projectLogo)));
            }

            if(cmd.hasOption(layoutPdf)) {
                parameters.setLayoutPdf(LayoutPdfEnum.valueOf(cmd.getOptionValue(layoutPdf)));
            }

            if(cmd.hasOption(htmlPanelToggle)) {
                parameters.setHtmlPanelToggle(HtmlPanelToggleEnum.valueOf(cmd.getOptionValue(htmlPanelToggle)));
            }

            if(cmd.hasOption(menuColor)) {
                parameters.setMenuColor(cmd.getOptionValue(menuColor));
            }

            if(cmd.hasOption(menuTextColor)) {
                parameters.setMenuTextColor(cmd.getOptionValue(menuTextColor));
            }

            if(cmd.hasOption(disableEmbedImages)) {
                parameters.setEmbedImages(false);
            }

            if(cmd.hasOption(compilationType)) {
                parameters.setCompilationType(CompilationTypeEnum.valueOf(cmd.getOptionValue(compilationType)));
            }

            if(cmd.hasOption(compilationArtifact)) {
                parameters.setCompilationArtifact(CompilationArtifactEnum.valueOf(cmd.getOptionValue(compilationArtifact)));
            }

            parameters.setProjectSource(new File(cmd.getOptionValue(projectSource)));

            if(cmd.hasOption(projectMasterSource)) {
                parameters.setProjectMasterSource(new File(cmd.getOptionValue(projectMasterSource)));
            }

            if(cmd.hasOption(projectTarget)) {
                parameters.setProjectTarget(new File(cmd.getOptionValue(projectTarget)));
            }

            parameters.validate();

            return parameters;
        } catch (ParseException | ParametersException e) {
            log.error(e.getMessage());
            new HelpFormatter().printHelp("Pirilampo", options);
        } catch (Throwable e) {
            log.error(log.getName(), e);
        }

        System.exit(1);
        return null;
    }
}
