package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.FeatureMetadataDto;
import br.com.pirilampo.core.dto.ParametersDto;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FeatureToHTMLCompiler extends Compiler {
    private final ParametersDto parameters;
    private final File feature;
    private final FeatureMetadataDto featureMetadataDto;

    public FeatureToHTMLCompiler(ParametersDto parameters) {
        this.parameters = parameters;
        this.feature = parameters.getProjectSource();
        this.featureMetadataDto = getFeatureMetadata(parameters, feature);
    }

    public void build() throws Exception {
        ParseDocument parseDocument = new ParseDocument(parameters, feature);

        try (
                FileOutputStream fos = new FileOutputStream(getOutArtifact(parameters));
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

            parseDocument.build(out);

            out.print("</div></div></div></body></html>");
        }
    }
}
