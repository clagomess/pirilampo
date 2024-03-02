package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.FeatureMetadataDto;
import br.com.pirilampo.core.dto.ParametroDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class FeatureToPDFCompiler extends Compiler {
    private final ParametroDto parametro;
    private final File feature;
    private final FeatureMetadataDto featureMetadataDto;

    public FeatureToPDFCompiler(ParametroDto parametro) {
        this.parametro = parametro;
        this.feature = parametro.getTxtSrcFonte();
        this.featureMetadataDto = getFeatureMetadata(parametro, feature);
    }

    public void build() throws Exception {
        File outFile = new File(
                (parametro.getTxtOutputTarget() != null ? parametro.getTxtOutputTarget() : new File(feature.getParent())),
                featureMetadataDto.getName() + ".pdf"
        );

        try(
                PipedOutputStream pos = new PipedOutputStream();
                PipedInputStream pis = new PipedInputStream(pos);

                // HTML
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(pos, StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(bw);

                // PDF
                FileOutputStream fos = new FileOutputStream(outFile);
                InputStream css = Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
                                .getResource("htmlTemplate/dist/feature-pdf.min.css"))
                        .openStream();
        ){
            List<CompletableFuture<Void>> list = new LinkedList<>();

            list.add(CompletableFuture.runAsync(() -> {
                try {
                    log.info("init-html");
                    out.print("<!DOCTYPE html><html lang=\"en\"><body>");
                    out.print("<h1 class=\"page-header\">");
                    out.print(String.format(
                            "%s <small>%s <em>%s</em></small>",
                            parametro.getTxtNome(),
                            featureMetadataDto.getName(),
                            parametro.getTxtVersao()
                    ));
                    out.print("</h1>");

                    new ParseDocument(parametro, feature).build(out);

                    out.print("</body></html>");

                    out.close();
                    log.info("end-html");
                }catch (Throwable e){
                    throw new RuntimeException(e);
                }
            }));

            list.add(CompletableFuture.runAsync(() -> {
                try {
                    log.info("init-pdf");
                    new ParsePdf().build(fos, pis, css, parametro.getTipLayoutPdf());
                    log.info("end-pdf");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));

            CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).get();
        }

        // @TODO: remove buffer file
    }
}
