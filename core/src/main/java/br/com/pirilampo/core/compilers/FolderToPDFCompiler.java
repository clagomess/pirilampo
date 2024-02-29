package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.ParametroDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FolderToPDFCompiler extends Compiler {
    private final ParametroDto parametro;
    
    public static final String HTML_FEATURE_PDF = "<h1 class=\"page-header\">%s <small>%s <em>%s</em></small></h1>\n" +
            "%s\n<span style=\"page-break-after: always\"></span>";

    public void build(ParametroDto parametro) throws Exception {
        StringBuilder html = new StringBuilder();

        // Abre pasta root
        File curDir = parametro.getTxtSrcFonte();

        // Popula com arquivos feature
        List<File> arquivos = listFolder(curDir);

        if(!arquivos.isEmpty()) {
            int progressNum = 1;
            for (File f : arquivos) {
                // progress
                // ProgressBind.setProgress(progressNum / (double) arquivos.size()); @TODO: check
                progressNum++;

                // compila
                String rawHtml = null; //@TODO ParseDocument.getFeatureHtml(parametro, f);

                html.append(String.format(
                        HTML_FEATURE_PDF,
                        parametro.getTxtNome(),
                        f.getName().replace(Resource.getExtension(f), ""),
                        parametro.getTxtVersao(),
                        rawHtml
                ));
            }

            //------------------ BUILD -----------------
            String htmlTemplate = Resource.loadResource("htmlTemplate/html/template_feature_pdf.html");
            String css = Resource.loadResource("htmlTemplate/dist/feature-pdf.min.css");

            html = new StringBuilder(htmlTemplate.replace("#HTML_TEMPLATE#", html));

            ParsePdf pp = new ParsePdf();

            String outDir = (parametro.getTxtOutputTarget() != null ? parametro.getTxtOutputTarget().getAbsolutePath() : curDir.getParent() + File.separator + "html");
            File outDirF = new File(outDir);

            if(!outDirF.exists()){
                outDirF.mkdir();
            }

            log.info("GERANDO PDF");
            // ProgressBind.setProgress(-1); @TODO: check

            pp.buildHtml(outDir + File.separator + "index.pdf", html.toString(), css, parametro.getTipLayoutPdf().getValue(), parametro.getTipPainel().getValue());
        }
    }
}
