package com.github.clagomess.pirilampo.cli;

import com.github.clagomess.pirilampo.core.compilers.FeatureToHTMLCompiler;
import com.github.clagomess.pirilampo.core.compilers.FeatureToPDFCompiler;
import com.github.clagomess.pirilampo.core.compilers.FolderToHTMLCompiler;
import com.github.clagomess.pirilampo.core.compilers.FolderToPDFCompiler;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import static com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum.HTML;
import static com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum.PDF;
import static com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum.*;

@Slf4j
public class Main {
    private static final MainOptions mainOptions = new MainOptions();

    public static void main(String[] args) {
        log.info("Pirilampo - Ver.: {}", Main.class.getPackage().getImplementationVersion()); //@TODO: fixcd

        try {
            ParametersDto parameters = mainOptions.getArgs(args);

            // @TODO: load properties

            if (parameters.getCompilationType() == FEATURE &&
                    parameters.getCompilationArtifact() == HTML
            ) {
                new FeatureToHTMLCompiler(parameters).build();
            }

            if (parameters.getCompilationType() == FEATURE &&
                    parameters.getCompilationArtifact() == PDF
            ) {
                new FeatureToPDFCompiler(parameters).build();
            }

            if (Arrays.asList(FOLDER, FOLDER_DIFF).contains(parameters.getCompilationType()) &&
                    parameters.getCompilationArtifact() == HTML
            ) {
                new FolderToHTMLCompiler(parameters).build();
            }

            if (parameters.getCompilationType() == FOLDER &&
                    parameters.getCompilationArtifact() == PDF
            ) {
                new FolderToPDFCompiler(parameters).build();
            }

            System.exit(0);
        } catch (Throwable e) {
            log.error(log.getName(), e);
            System.exit(1);
        }
    }
}
