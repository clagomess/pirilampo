package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.bean.Indice;
import br.com.pirilampo.core.constant.HtmlTemplate;
import br.com.pirilampo.core.dto.ParametroDto;
import br.com.pirilampo.core.enums.DiffEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Compilador {
    public void compilarPasta(ParametroDto parametro) throws Exception {
        ParseMenu parseMenu = new ParseMenu(parametro);
        StringBuilder htmlTemplate = new StringBuilder();
        StringBuilder htmlJavascript = new StringBuilder();
        StringBuilder htmlCss = new StringBuilder();
        List<File> paginaHtmlAnexo = new ArrayList<>();
        Map<String, Indice> indice = new HashMap<>();

        // -------- MASTER
        List<File> arquivosMaster = null;
        if(!StringUtils.isEmpty(parametro.getTxtSrcFonteMaster())) {
            // Abre pasta root
            File curDirMaster = new File(parametro.getTxtSrcFonteMaster());

            // Popula com arquivos feature
            arquivosMaster = ListarPasta.listarPasta(curDirMaster);
        }

        // -------- NORMAL
        // Abre pasta root
        File curDir = new File(parametro.getTxtSrcFonte());

        // Popula com arquivos feature
        final List<File> arquivos = ListarPasta.listarPasta(curDir);

        if(arquivos.size() > 0){
            int progressNum = 1;

            for(File f : arquivos){
                // progress
                //ProgressBind.setProgress(progressNum / (double) arquivos.size()); @TODO: check
                progressNum++;

                // monta nome menu
                final String featureIdHtml = Feature.idHtml(parametro, f);
                final String featureIdFeature = Feature.idFeature(parametro, f);
                DiffEnum diff = DiffEnum.NAO_COMPARADO;

                // Processa Master
                if(!StringUtils.isEmpty(parametro.getTxtSrcFonteMaster())) {
                    diff = DiffEnum.NOVO;
                    File fmd = null;

                    if(arquivosMaster != null && !arquivosMaster.isEmpty()) {
                        for (File fm : arquivosMaster) {
                            String absoluteNFM = Resource.absoluteNameFeature(parametro.getTxtSrcFonteMaster(), fm.getAbsolutePath());
                            String absoluteNFB = Resource.absoluteNameFeature(parametro.getTxtSrcFonte(), f.getAbsolutePath());
                            String featureM = Resource.loadFeature(fm.getAbsolutePath());
                            String featureB = Resource.loadFeature(f.getAbsolutePath());

                            if (absoluteNFM.equals(absoluteNFB)) {
                                if(featureM.equals(featureB)){
                                    diff = DiffEnum.IGUAL;
                                }else{
                                    diff = DiffEnum.DIFERENTE;
                                    fmd = fm;
                                }
                                break;
                            }
                        }
                    }

                    log.info("Diff Master/Branch: {} - {}", diff, f.getAbsolutePath());

                    // pula para o proximo
                    if(diff.equals(DiffEnum.IGUAL)){
                        continue;
                    }

                    if(fmd != null) {
                        final String featureHtml = null; //@TODO ParseDocument.getFeatureHtml(parametro, fmd);

                        htmlTemplate.append(String.format(HtmlTemplate.HTML_TEMPLATE, "master_" + featureIdHtml, featureHtml));
                        htmlTemplate.append(String.format(HtmlTemplate.HTML_TEMPLATE, "master_" + featureIdFeature, Resource.loadFeature(fmd.getAbsolutePath())));
                    }
                }

                // Gera a feture
                ParseDocument pd = new ParseDocument(parametro, f);
                String featureHtml = null; //@TODO pd.getFeatureHtml(parametro.getTipPainel().getValue());
                paginaHtmlAnexo.addAll(pd.getPaginaHtmlAnexo());
                indice.putAll(pd.getIndice());

                htmlTemplate.append(String.format(HtmlTemplate.HTML_TEMPLATE, featureIdHtml, featureHtml));

                // Adiciona item de menu se deu tudo certo com a master
                parseMenu.addMenuItem(f, diff, pd.getFeatureTitulo());

                // Salva as feature para diff
                if(!StringUtils.isEmpty(parametro.getTxtSrcFonteMaster())){
                    htmlTemplate.append(String.format(HtmlTemplate.HTML_TEMPLATE, featureIdFeature, Resource.loadFeature(f.getAbsolutePath())));
                }
            }

            // adiciona html embed
            for (File htmlEmbed : paginaHtmlAnexo){
                String loadedHtmlEmbed = Resource.loadFeature(htmlEmbed.getAbsolutePath());
                htmlTemplate.append(String.format(
                        "<template type=\"text/ng-template\" id=\"%s\">%s</template>%n",
                        htmlEmbed.getName(),
                        loadedHtmlEmbed
                ));
            }

            //------------------ BUILD -----------------
            String html = Resource.loadResource("htmlTemplate/html/template_feature_pasta.html");

            // monta indice
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(indice);
            htmlJavascript.append(String.format(HtmlTemplate.HTML_JAVASCRIPT, String.format("var indice = %s;", json)));

            // adiciona resources
            htmlCss.append(String.format(HtmlTemplate.HTML_CSS, Resource.loadResource("htmlTemplate/dist/feature-pasta.min.css")));
            htmlJavascript.append(String.format(HtmlTemplate.HTML_JAVASCRIPT, Resource.loadResource("htmlTemplate/dist/feature-pasta.min.js")));
            htmlJavascript.append(String.format(HtmlTemplate.HTML_JAVASCRIPT, Resource.loadResource("htmlTemplate/dist/feature-pasta-angular.min.js")));

            html = html.replace("#PROJECT_NAME#", parametro.getTxtNome());
            html = html.replace("#PROJECT_VERSION#", parametro.getTxtVersao());
            html = html.replace("#HTML_MENU#", parseMenu.getHtml());
            html = html.replace("#HTML_CSS#", htmlCss);
            html = html.replace("#HTML_JAVASCRIPT#", htmlJavascript);
            html = html.replace("#HTML_TEMPLATE#", htmlTemplate);
            html = html.replace("#MENU_COLOR#", parametro.getClrMenu());
            html = html.replace("#MENU_TEXT_COLOR#", parametro.getClrTextoMenu());

            // monta cabeçalho menu
            if(!StringUtils.isEmpty(parametro.getTxtLogoSrc())){
                String logoString = ParseImage.parse(parametro, new File(parametro.getTxtLogoSrc()));
                html = html.replace("#PROJECT_LOGO#", String.format("<img class=\"logo\" src=\"%s\">", logoString));
            }else{
                html = html.replace("#PROJECT_LOGO#", String.format(
                        "%s <small><em>%s</em></small>",
                        parametro.getTxtNome(),
                        parametro.getTxtVersao()
                ));
            }

            // Grava
            // Cria Diretório se não existir */html/feature/
            String outDir = (StringUtils.isNotEmpty(parametro.getTxtOutputTarget()) ? parametro.getTxtOutputTarget() : curDir.getParent() + File.separator + "html");
            File outDirF = new File(outDir);

            if(!outDirF.exists()){
                outDirF.mkdir();
            }

            Resource.writeHtml(html, outDir + File.separator + "index.html");
        }
    }

    public void compilarFeature(ParametroDto parametro) throws Exception {
        File feature = new File(parametro.getTxtSrcFonte());
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
                    feature.getName().replace(Resource.getExtension(feature), ""),
                    parametro.getTxtVersao()
            ));

            parseDocument.build(out);

            out.print("</div></div></div></body></html>");
        }
    }

    public void compilarFeaturePdf(ParametroDto parametro) throws Exception {
        // Abre feature
        File feature = new File(parametro.getTxtSrcFonte());

        //------------------ BUILD -----------------
        String htmlTemplate = Resource.loadResource("htmlTemplate/html/template_feature_pdf.html");
        String css = Resource.loadResource("htmlTemplate/dist/feature-pdf.min.css");
        String html = null; //@TODO ParseDocument.getFeatureHtml(parametro, feature);

        html = String.format(
                HtmlTemplate.HTML_FEATURE_PDF,
                parametro.getTxtNome(),
                feature.getName().replace(Resource.getExtension(feature), ""),
                parametro.getTxtVersao(),
                html
        );

        html = htmlTemplate.replace("#HTML_TEMPLATE#", html);

        ParsePdf pp = new ParsePdf();

        String path = (StringUtils.isNotEmpty(parametro.getTxtOutputTarget()) ? parametro.getTxtOutputTarget() : feature.getParent());
        path += File.separator + feature.getName().replace(Resource.getExtension(feature), "") + ".pdf";

        pp.buildHtml(path, html, css, parametro.getTipLayoutPdf().getValue(), parametro.getTipPainel().getValue());
    }

    public void compilarPastaPdf(ParametroDto parametro) throws Exception {
        StringBuilder html = new StringBuilder();

        // Abre pasta root
        File curDir = new File(parametro.getTxtSrcFonte());

        // Popula com arquivos feature
        List<File> arquivos = ListarPasta.listarPasta(curDir);

        if(!arquivos.isEmpty()) {
            int progressNum = 1;
            for (File f : arquivos) {
                // progress
                // ProgressBind.setProgress(progressNum / (double) arquivos.size()); @TODO: check
                progressNum++;

                // compila
                String rawHtml = null; //@TODO ParseDocument.getFeatureHtml(parametro, f);

                html.append(String.format(
                        HtmlTemplate.HTML_FEATURE_PDF,
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

            String outDir = (StringUtils.isNotEmpty(parametro.getTxtOutputTarget()) ? parametro.getTxtOutputTarget() : curDir.getParent() + File.separator + "html");
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
