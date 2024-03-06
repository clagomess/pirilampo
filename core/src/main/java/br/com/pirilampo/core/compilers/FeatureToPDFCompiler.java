package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.FeatureMetadataDto;
import br.com.pirilampo.core.dto.ParametersDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class FeatureToPDFCompiler extends Compiler {
    private final ParametersDto parameters;
    private final File feature;
    private final FeatureMetadataDto featureMetadataDto;

    public FeatureToPDFCompiler(ParametersDto parameters) {
        this.parameters = parameters;
        this.feature = parameters.getProjectSource();
        this.featureMetadataDto = getFeatureMetadata(parameters, feature);
    }

    public void build() throws Exception {
        File bufferHtml = File.createTempFile("pirilampo-buffer-", ".html");
        log.info("Created buffer file: {}", bufferHtml);

        try (
                FileOutputStream fos = new FileOutputStream(bufferHtml);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(bw);
        ){
            out.print("<!DOCTYPE html><html lang=\"en\"><body>");
            out.print("<h1 class=\"page-header\">");
            out.print(String.format(
                    "%s <small>%s <em>%s</em></small>",
                    parameters.getProjectName(),
                    featureMetadataDto.getName(),
                    parameters.getProjectVersion()
            ));
            out.print("</h1>");

            new ParseDocument(parameters, feature).build(out);

            out.print("</body></html>");
        }

        // @TODO: maibe a pipe with these streams?

        try (
                FileOutputStream fos = new FileOutputStream(getOutArtifact(parameters));
                InputStream html = Files.newInputStream(bufferHtml.toPath());
                InputStream css = Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
                                .getResource("htmlTemplate/dist/feature-pdf.min.css"))
                                .openStream();
        ){
            new ParsePdf().build(fos, html, css, parameters.getLayoutPdf());
        }

        // @TODO: remove buffer file
    }
}
