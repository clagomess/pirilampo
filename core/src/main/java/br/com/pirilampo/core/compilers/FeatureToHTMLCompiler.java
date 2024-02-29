package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.FeatureMetadataDto;
import br.com.pirilampo.core.dto.ParametroDto;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FeatureToHTMLCompiler extends Compiler {
    private final ParametroDto parametro;
    private final File feature;
    private final FeatureMetadataDto featureMetadataDto;

    public FeatureToHTMLCompiler(ParametroDto parametro) {
        this.parametro = parametro;
        this.feature = new File(parametro.getTxtSrcFonte());
        this.featureMetadataDto = getFeatureMetadata(parametro, feature);
    }

    public void build() throws Exception {
        ParseDocument parseDocument = new ParseDocument(parametro, feature);

        String outFile = String.format(
                "%s%s%s.html",
                File.separator,
                StringUtils.isNotEmpty(parametro.getTxtOutputTarget()) ?
                        parametro.getTxtOutputTarget() :
                        feature.getParent(),
                feature.getName().replace(Resource.getExtension(feature), "")
        );

        try (
                FileOutputStream fos = new FileOutputStream(outFile);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(bw);
        ){
            out.print("<!DOCTYPE html><html lang=\"en\"><head>");
            out.print("<meta charset=\"utf-8\">");
            out.print("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
            out.print("<meta name=\"viewport\" content=\"width=device-width, shrink-to-fit=no, initial-scale=1\">");
            out.print(String.format("<title>%s</title>", parametro.getTxtNome()));
            out.print("<style>");
            out.print(Resource.loadResource("htmlTemplate/dist/feature.min.css")); //@TODO: está usando muita memoria
            out.print("</style>");
            out.print("</head><body>");
            out.print("<div class=\"container\"><div class=\"row\"><div class=\"col-sm-12\">");
            out.print(String.format(
                    "<h1 class=\"page-header\">%s <small>%s <em>%s</em></small></h1>",
                    parametro.getTxtNome(),
                    featureMetadataDto.getName(),
                    parametro.getTxtVersao()
            ));

            parseDocument.build(out);

            out.print("</div></div></div></body></html>");
        }
    }
}
