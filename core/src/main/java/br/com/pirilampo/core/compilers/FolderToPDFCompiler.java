package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.ParametersDto;
import br.com.pirilampo.core.parsers.GherkinDocumentParser;
import br.com.pirilampo.core.parsers.PdfParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class FolderToPDFCompiler extends Compiler {
    private final ParametersDto parameters;

    public void build() throws Exception {
        List<File> arquivos = listFolder(parameters.getProjectSource());
        if(arquivos.isEmpty()) return;

        File bufferHtml = File.createTempFile("pirilampo-buffer-", ".html");
        log.info("Created buffer file: {}", bufferHtml);

        try (
                FileOutputStream fos = new FileOutputStream(bufferHtml);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(bw);
        ){
            out.print("<!DOCTYPE html><html lang=\"en\"><body>");

            for (File feature : arquivos) {
                out.print("<h1 class=\"page-header\">");
                out.print(String.format(
                        "%s <small>%s <em>%s</em></small>",
                        parameters.getProjectName(),
                        getFeatureMetadata(parameters, feature).getName(),
                        parameters.getProjectVersion()
                ));
                out.print("</h1>");

                new GherkinDocumentParser(parameters, feature).build(out);

                out.print("<span style=\"page-break-after: always\"></span>");
            }

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
            new PdfParser().build(fos, html, css, parameters.getLayoutPdf());
        }

        // @TODO: remove buffer file
        // @TODO: impl. PDF Index
    }
}
