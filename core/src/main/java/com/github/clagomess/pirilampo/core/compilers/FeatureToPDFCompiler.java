package com.github.clagomess.pirilampo.core.compilers;

import com.github.clagomess.pirilampo.core.dto.FeatureMetadataDto;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.parsers.GherkinDocumentParser;
import com.github.clagomess.pirilampo.core.parsers.PdfParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

import static com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum.PDF;
import static com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum.FEATURE;

@Slf4j
@RequiredArgsConstructor
public class FeatureToPDFCompiler extends Compiler {
    private final PropertiesCompiler propertiesCompiler = new PropertiesCompiler();
    private final ParametersDto parameters;
    private final File feature;
    private final FeatureMetadataDto featureMetadataDto;

    public FeatureToPDFCompiler(ParametersDto parameters) {
        if(parameters.getCompilationType() != FEATURE || parameters.getCompilationArtifact() != PDF){
            throw new RuntimeException("Wrong compilation parameters");
        }

        this.parameters = parameters;
        this.feature = parameters.getProjectSource();
        this.featureMetadataDto = getFeatureMetadata(parameters, feature);
    }

    public void build() throws Exception {
        startTimer();

        File bufferHtml = createTempFile();
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

                new GherkinDocumentParser(parameters, feature).build(out);

                out.print("</body></html>");
            }

            if(bufferHtml.exists()){
                try(InputStream html = Files.newInputStream(bufferHtml.toPath())) {
                    pdfParser.addFeatureHTML(feature, html);
                }
            }

            pdfParser.closeDocument();
        } catch (Throwable e){
            outArtifact.delete();
            deleteAllTempFiles();
            throw e;
        } finally {
            propertiesCompiler.setData(parameters);
            stopTimer();
        }
    }
}
