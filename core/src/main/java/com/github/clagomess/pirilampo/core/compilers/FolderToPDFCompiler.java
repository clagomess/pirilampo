package com.github.clagomess.pirilampo.core.compilers;

import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.parsers.GherkinDocumentParser;
import com.github.clagomess.pirilampo.core.parsers.PdfParser;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Set;

import static com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum.PDF;
import static com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum.FOLDER;

@Slf4j
public class FolderToPDFCompiler extends Compiler {
    private final PropertiesCompiler propertiesCompiler = new PropertiesCompiler();
    private final ParametersDto parameters;

    public FolderToPDFCompiler(ParametersDto parameters) {
        if(parameters.getCompilationType() != FOLDER || parameters.getCompilationArtifact() != PDF){
            throw new RuntimeException("Wrong compilation parameters");
        }

        this.parameters = parameters;
    }

    public void build() throws Exception {
        startTimer();
        progressCount = 0;
        buttons.setEnabled(false);
        progress.setProgress(-1);

        Set<File> arquivos = listFolder(parameters.getProjectSource());
        if(arquivos.isEmpty()) return;

        File outArtifact = getOutArtifact(parameters);

        try (
                FileOutputStream fosPDF = new FileOutputStream(outArtifact);
                InputStream css = Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
                                .getResource("htmlTemplate/dist/feature-pdf.min.css"))
                        .openStream();
        ){
            parameters.setEmbedImages(false);

            PdfParser pdfParser = new PdfParser(parameters, css);
            pdfParser.initDocument(fosPDF);

            for (File feature : arquivos) {
                File bufferHtml = createTempFile();

                try (
                        FileOutputStream fosHTML = new FileOutputStream(bufferHtml);
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fosHTML, StandardCharsets.UTF_8));
                        PrintWriter out = new PrintWriter(bw);
                ) {
                    // @TODO: improve titles for pdf index
                    out.print("<!DOCTYPE html><html lang=\"en\"><body>");
                    out.print("<h1 class=\"page-header\">");
                    out.print(String.format(
                            "%s <small>%s <em>%s</em></small>",
                            parameters.getProjectName(),
                            getFeatureMetadata(parameters, feature).getName(),
                            parameters.getProjectVersion()
                    ));
                    out.print("</h1>");

                    GherkinDocumentParser parser = new GherkinDocumentParser(parameters, feature);
                    parser.build(out);

                    out.print("<span style=\"page-break-after: always\"></span>");

                    if(!parser.getPaginaHtmlAnexo().isEmpty()){
                        for(File html : parser.getPaginaHtmlAnexo()){
                            log.info("- appending: {}", html);
                            out.print(String.format("<h1>%s</h1>", html.getName()));
                            writeFileToOut(html, out);
                            out.print("<span style=\"page-break-after: always\"></span>");
                        }
                    }

                    out.print("</body></html>");
                }

                if(bufferHtml.exists()){
                    try(InputStream html = Files.newInputStream(bufferHtml.toPath())) {
                        pdfParser.addFeatureHTML(feature, html);
                    }
                }

                // progress
                progressCount++;
                progress.setProgress(progressCount / arquivos.size());
            }

            pdfParser.closeDocument();
        } catch (Throwable e){
            outArtifact.delete();
            deleteAllTempFiles();
            throw e;
        } finally {
            propertiesCompiler.setData(parameters);
            buttons.setEnabled(true);
            progressCount = 0;
            progress.setProgress(0);
            stopTimer();
        }
    }
}
