package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.ParametroDto;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class FeatureToPDFCompiler {
    private final ParametroDto parametro;

    public static final String HTML_FEATURE_PDF = "<h1 class=\"page-header\">%s <small>%s <em>%s</em></small></h1>\n" +
            "%s\n<span style=\"page-break-after: always\"></span>";

    public void build() throws Exception {
        // Abre feature
        File feature = parametro.getTxtSrcFonte();

        //------------------ BUILD -----------------
        String htmlTemplate = Resource.loadResource("htmlTemplate/html/template_feature_pdf.html");
        String css = Resource.loadResource("htmlTemplate/dist/feature-pdf.min.css");
        String html = null; //@TODO ParseDocument.getFeatureHtml(parametro, feature);

        html = String.format(
                HTML_FEATURE_PDF,
                parametro.getTxtNome(),
                feature.getName().replace(Resource.getExtension(feature), ""),
                parametro.getTxtVersao(),
                html
        );

        html = htmlTemplate.replace("#HTML_TEMPLATE#", html);

        ParsePdf pp = new ParsePdf();

        String path = (parametro.getTxtOutputTarget() != null ? parametro.getTxtOutputTarget().getAbsolutePath() : feature.getParent());
        path += File.separator + feature.getName().replace(Resource.getExtension(feature), "") + ".pdf";

        pp.buildHtml(path, html, css, parametro.getTipLayoutPdf().getValue(), parametro.getTipPainel().getValue());
    }
}
