package com.github.clagomess.pirilampo.core.compilers;

import com.github.clagomess.pirilampo.core.dto.FeatureMetadataDto;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.parsers.GherkinDocumentParser;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum.HTML;
import static com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum.FEATURE;

public class FeatureToHTMLCompiler extends Compiler implements ArtifactCompiler {
    private final PropertiesCompiler propertiesCompiler = new PropertiesCompiler();
    private final ParametersDto parameters;
    private final File feature;
    private final FeatureMetadataDto featureMetadataDto;

    public FeatureToHTMLCompiler(ParametersDto parameters) {
        if(parameters.getCompilationType() != FEATURE || parameters.getCompilationArtifact() != HTML){
            throw new RuntimeException("Wrong compilation parameters");
        }

        this.parameters = parameters;
        this.feature = parameters.getProjectSource();
        this.featureMetadataDto = getFeatureMetadata(parameters, feature);
    }

    public void build() throws Exception {
        startTimer();
        progress.setProgress(-1);

        GherkinDocumentParser gherkinDocumentParser = new GherkinDocumentParser(parameters, feature);

        File outArtifact = getOutArtifact(parameters);

        try (
                FileOutputStream fos = new FileOutputStream(outArtifact);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(bw);
        ){
            out.print("<!DOCTYPE html><html lang=\"en\"><head>");
            out.print("<meta charset=\"utf-8\">");
            out.print("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
            out.print("<meta name=\"viewport\" content=\"width=device-width, shrink-to-fit=no, initial-scale=1\">");
            out.print(String.format("<title>%s</title>", parameters.getProjectName()));
            out.print("<style>");
            writeResourceToOut("htmlTemplate/dist/feature.min.css", out);
            out.print("</style>");
            out.print("</head><body>");
            out.print("<div class=\"container\"><div class=\"row\"><div class=\"col-sm-12\">");
            out.print(String.format(
                    "<h1 class=\"page-header\">%s <small>%s <em>%s</em></small></h1>",
                    parameters.getProjectName(),
                    featureMetadataDto.getName(),
                    parameters.getProjectVersion()
            ));

            gherkinDocumentParser.build(out);

            out.print("</div></div></div></body></html>");
        } catch (Throwable e){
            outArtifact.delete();
            throw e;
        } finally {
            propertiesCompiler.setData(parameters);
            progress.setProgress(0);
            stopTimer();
        }
    }
}
