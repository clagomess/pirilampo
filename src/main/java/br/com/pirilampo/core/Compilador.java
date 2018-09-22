package br.com.pirilampo.core;

import br.com.pirilampo.bean.Indice;
import br.com.pirilampo.bean.Parametro;
import br.com.pirilampo.constant.HtmlTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Compilador {
    public static StringBuilder LOG;

    public Compilador(){
        Compilador.LOG = new StringBuilder();
    }

    public void compilarPasta(Parametro parametro) throws Exception {
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
            for(File f : arquivos){
                // monta nome menu
                final String featureIdHtml = Feature.idHtml(parametro, f);
                final String featureIdFeature = Feature.idFeature(parametro, f);

                // Processa Master
                if(!StringUtils.isEmpty(parametro.getTxtSrcFonteMaster())) {
                    boolean diferente = true;
                    File fmd = null;

                    if(arquivosMaster != null && !arquivosMaster.isEmpty()) {
                        for (File fm : arquivosMaster) {
                            String absoluteNFM = Resource.absoluteNameFeature(parametro.getTxtSrcFonteMaster(), fm.getAbsolutePath());
                            String absoluteNFB = Resource.absoluteNameFeature(parametro.getTxtSrcFonte(), f.getAbsolutePath());
                            String featureM = Resource.loadFeature(fm.getAbsolutePath());
                            String featureB = Resource.loadFeature(f.getAbsolutePath());

                            if (absoluteNFM.equals(absoluteNFB)) {
                                if(featureM.equals(featureB)){
                                    diferente = false;
                                }else{
                                    fmd = fm;
                                    log.info("Diff Master/Branch: {} - {} - {} - {}", featureM.hashCode(), featureB.hashCode(), absoluteNFM, absoluteNFB);
                                }
                                break;
                            }
                        }
                    }

                    // pula para o proximo
                    if(!diferente){
                        continue;
                    }

                    if(fmd != null) {
                        final String featureHtml = ParseDocument.getFeatureHtml(parametro, fmd);

                        htmlTemplate.append(String.format(HtmlTemplate.HTML_TEMPLATE, "master_" + featureIdHtml, featureHtml));
                        htmlTemplate.append(String.format(HtmlTemplate.HTML_TEMPLATE, "master_" + featureIdFeature, Resource.loadFeature(fmd.getAbsolutePath())));
                    }
                }

                // Adiciona item de menu se deu tudo certo com a master
                parseMenu.addMenuItem(f);

                // Gera a feture
                ParseDocument pd = new ParseDocument(parametro, f);
                String featureHtml = pd.getFeatureHtml();
                paginaHtmlAnexo.addAll(pd.getPaginaHtmlAnexo());
                indice.putAll(pd.getIndice());

                htmlTemplate.append(String.format(HtmlTemplate.HTML_TEMPLATE, featureIdHtml, featureHtml));

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

    public void compilarFeature(Parametro parametro) throws IOException {
        // Abre feature
        File feature = new File(parametro.getTxtSrcFonte());

        // compila
        String featureHtml = ParseDocument.getFeatureHtml(parametro, feature);

        //------------------ BUILD -----------------
        String html = Resource.loadResource("htmlTemplate/html/template_feature.html");

        String htmlCss = String.format(HtmlTemplate.HTML_CSS, Resource.loadResource("htmlTemplate/dist/feature.min.css"));

        html = html.replace("#PROJECT_NAME#", parametro.getTxtNome());
        html = html.replace("#PROJECT_VERSION#", parametro.getTxtVersao());
        html = html.replace("#PROJECT_FEATURE#", feature.getName().replace(Resource.getExtension(feature), ""));
        html = html.replace("#HTML_CSS#", htmlCss);
        html = html.replace("#HTML_TEMPLATE#", featureHtml);

        // Grava
        String outDir = (StringUtils.isNotEmpty(parametro.getTxtOutputTarget()) ? parametro.getTxtOutputTarget() : feature.getParent());
        outDir += File.separator + feature.getName().replace(Resource.getExtension(feature), "") + ".html";

        Resource.writeHtml(html, outDir);
    }

    public void compilarFeaturePdf(Parametro parametro) throws Exception {
        // Abre feature
        File feature = new File(parametro.getTxtSrcFonte());

        //------------------ BUILD -----------------
        String htmlTemplate = Resource.loadResource("htmlTemplate/html/template_feature_pdf.html");
        String css = Resource.loadResource("htmlTemplate/dist/feature-pdf.min.css");
        String html = ParseDocument.getFeatureHtml(parametro, feature);

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

        pp.buildHtml(path, html, css, parametro.getTipLayoutPdf().getValue());
    }

    public void compilarPastaPdf(Parametro parametro) throws Exception {
        StringBuilder html = new StringBuilder();

        // Abre pasta root
        File curDir = new File(parametro.getTxtSrcFonte());

        // Popula com arquivos feature
        List<File> arquivos = ListarPasta.listarPasta(curDir);

        if(!arquivos.isEmpty()) {
            for (File f : arquivos) {
                String rawHtml = ParseDocument.getFeatureHtml(parametro, f);

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

            pp.buildHtml(outDir + File.separator + "index.pdf", html.toString(), css, parametro.getTipLayoutPdf().getValue());
        }
    }
}
