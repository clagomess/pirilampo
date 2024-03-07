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
    private final ParametersDto parameters;

    public FolderToPDFCompiler(ParametersDto parameters) {
        if(parameters.getCompilationType() != FOLDER || parameters.getCompilationArtifact() != PDF){
            throw new RuntimeException("Wrong compilation parameters");
        }

        this.parameters = parameters;
    }

    public void build() throws Exception {
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
                File bufferHtml = File.createTempFile("pirilampo-buffer-", ".html");
                log.info("Created buffer file: {}", bufferHtml);

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
                } catch (Throwable e){
                    bufferHtml.delete();
                    throw e;
                } finally {
                    if(bufferHtml.exists()){
                        pdfParser.addFeatureHTML(feature, Files.newInputStream(bufferHtml.toPath())); //@TODO: not closeable
                    }

                    bufferHtml.delete();
                }
            }

            pdfParser.closeDocument();
        } catch (Throwable e){
            outArtifact.delete();
            throw e;
        }

        // @TODO: add done and took
        // @TODO: attach HTML at END
    }
}